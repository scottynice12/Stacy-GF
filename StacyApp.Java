import java.util.Scanner;

public class StacyApp {
    public static void main(String[] args) {
        StacyEngine stacy = new StacyEngine();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=================================================");
        System.out.println(" Stacy Engine Initialized (JDK 26 Architecture) ");
        System.out.println(" Try typing things like 'I am tired' or 'mess'    ");
        System.out.println("=================================================\n");

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("exit") || userInput.equalsIgnoreCase("quit")) {
                System.out.println("[System]: Conversation terminated.");
                break;
            }

            try {
                StacyEngine.Interaction intent;

                if (userInput.toLowerCase().contains("mess") || userInput.toLowerCase().contains("dirty")) {
                    intent = new StacyEngine.HouseworkAlert(9, "Kitchen/Living Room");
                } else {
                    intent = new StacyEngine.ConversationalChat(userInput);
                }

                String reply = stacy.generateResponse(intent);
                System.out.println(reply + "\n");

            } catch (Exception e) {
                System.out.println("[System Error]: " + e.getMessage());
            }
        }
        scanner.close();
    }
}
