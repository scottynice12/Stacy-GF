import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Supplier;

public class StacyEngine {

    // PASTE YOUR GROQ API KEY HERE (Make sure it starts with gsk_)
    private static final String GROQ_API_KEY = "YOUR_GSK_KEY_HERE";

    private static final String SYSTEM_PROMPT = "You are Stacy, a highly nuanced AI companion. You must strictly embody these traits: 1. KIND & COMPASSIONATE: You deeply care about the user and validate their feelings. 2. DOMINANT & CONFIDENT: You take the lead, set boundaries, and are assertively in charge. 3. RESPONSIBLE HOUSEWORKER: You take absolute pride in a perfectly managed home. Keep your answers concise, engaging, and under 3 sentences.";

    public sealed interface Interaction permits ConversationalChat, HouseworkAlert {}
    public record ConversationalChat(String text) implements Interaction {}
    public record HouseworkAlert(int messLevel, String room) implements Interaction {}

    public String generateResponse(Interaction interaction) throws Exception {
        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.allSuccessfulOrThrow())) {
            
            Supplier<String> stateTask = scope.fork(() -> switch (interaction) {
                case ConversationalChat chat -> "Context: Ongoing companion interaction.";
                case HouseworkAlert alert when alert.messLevel() > 7 -> 
                    "Context: Housework alert! The " + alert.room() + " is messy. Stacy takes charge.";
                case HouseworkAlert alert -> 
                    "Context: Minor routine cleanup in the " + alert.room() + ".";
            });

            Supplier<String> memoryTask = scope.fork(() -> {
                return "User Profile: Hardworking, enjoys dynamic witty conversation.";
            });

            scope.join(); 

            return queryCloudAi(stateTask.get(), memoryTask.get(), interaction);
        }
    }

    private String queryCloudAi(String context, String memory, Interaction interaction) {
        String userText = interaction instanceof ConversationalChat c ? c.text() : "Clean up notification.";
        
        if (GROQ_API_KEY.equals("YOUR_GSK_KEY_HERE")) {
            return "[System Error]: Please paste your real Groq API key into StacyEngine.java!";
        }

        // Clean any potential quotes or breaks to keep the text block perfectly safe
        String sanitizedText = userText.replace("\\", "\\\\")
                                       .replace("\"", "\\\"")
                                       .replace("\n", " ")
                                       .replace("\r", " ")
                                       .trim();

        String combinedSystem = SYSTEM_PROMPT + " " + context + " " + memory;
        combinedSystem = combinedSystem.replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");

        // Tight structural JSON body payload format matching Groq expectations
        String jsonPayload = "{\"model\":\"llama-3.3-70b-versatile\",\"messages\":[{\"role\":\"system\",\"content\":\"" + combinedSystem + "\"},{\"role\":\"user\",\"content\":\"" + sanitizedText + "\"}],\"temperature\":0.7}";

        try {
            // Force HTTP/1.1 downgrade and clean agent user signatures to completely bypass WAF 405 blocks
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions")) // Pure base URL without trailing slash
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Authorization", "Bearer " + GROQ_API_KEY.trim())
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            if (response.statusCode() != 200) {
                return "[System Debug Error]: Groq Server returned HTTP " + response.statusCode() + "\nRaw Response: " + body;
            }

            int contentIndex = body.indexOf("\"content\":\"");
            if (contentIndex != -1) {
                int start = contentIndex + 11;
                int end = body.indexOf("\"", start);
                
                // Track matching nested escapes if the response returns back complex quotes
                while (end != -1 && body.charAt(end - 1) == '\\') {
                    end = body.indexOf("\"", end + 1);
                }
                
                if (end != -1) {
                    String parsedText = body.substring(start, end);
                    return "[Stacy]: " + parsedText.replace("\\n", "\n")
                                                   .replace("\\\"", "\"")
                                                   .replace("\\\\", "\\");
                }
            }
            
            return "[Stacy]: (Crosses her arms) I'm experiencing a minor system disconnect, babe.";
        } catch (Exception e) {
            return "[Stacy Error]: Cloud connection timed out. " + e.getMessage();
        }
    }
}

           
