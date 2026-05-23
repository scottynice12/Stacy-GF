# Advanced AI Companion Agentic Platform (JDK 26 Architecture)

A high-performance, type-safe command-line application built to demonstrate advanced Java 26 backend architecture. Moving beyond simple AI wrappers, 
this project leverages the cutting-edge Java concurrency stack and modern type systems to orchestrate
multi-threaded contextual state streaming with zero third-party framework dependencies.

---

## 🚀 Technical Highlights & Core Architecture

* **Structured Concurrency (JEP 525):** Utilizes `StructuredTaskScope` to execute contextual state processing and user-profile retrieval in parallel.
*  This keeps background subtasks safely isolated, reduces application latency, and eliminates thread-leaking hazards.
* **Type-Safe Domain Modeling:** Replaces brittle string-matching pipelines with modern Java **Sealed Interfaces** and **Records**.
* Application states (`ConversationalChat` vs. `HouseworkAlert`) are evaluated natively at compile-time.
* **Pattern Matching for Switch (JEP 530):** Uses pattern refinements within switch blocks to dynamically
* classify input intents and branch application behaviors cleanly without manual object casting.
* **Low-Overhead Native Networking Stack:** Implements a zero-dependency `HttpClient` pipeline configured to
* securely format, sanitize, and transmit compact JSON payloads to high-speed cloud inferencing clusters (Groq/Llama-3.3).

---

## 🛠️ Codebase & File Architecture

The entire platform is engineered into exactly two standalone, decoupled source files. 
By avoiding bloated web frameworks, the architecture cleanly separates the execution environment from the core business logic.

### 1. `StacyEngine.java` (The Core Architectural Layer)
This file governs the entire backend pipeline, multi-threaded orchestration, domain safety, and cloud network integration. It acts as the "brain" of the platform:
* **Domain State Modeling:** Contains a `sealed interface Interaction` restricted exclusively to `ConversationalChat` and `HouseworkAlert` records.
* This guarantees strict compile-time type safety, ensuring the processing pipeline can never ingest an invalid input format.
* **Parallel Orchestration Scope:** Implements JDK 26 `StructuredTaskScope.open()` using an `allSuccessfulOrThrow` joiner policy.
*  When processing an input, it forks two separate threads to run simultaneously: one that dynamically evaluates scenario rules,
*  and another that simulates a low-latency database lookup for user profile context.
* **Network Gateway Layer:** Hosts a fine-tuned native `HttpClient` forced to use `HTTP_1_1` compatibility protocols and matching
*  `User-Agent` string signatures. It automatically sanitizes dangerous punctuation characters
*  to prevent raw JSON injection attacks before dispatching requests to the cloud AI cluster.

### 2. `StacyApp.java` (The Runtime Command Center)
This file represents the application presentation layer, providing a lightweight, low-overhead command-line interface (CLI):
* **Asynchronous Input Collection:** Drives an active terminal listening state using a `Scanner` block inside an optimized loop. This allows the system to receive live user responses continuously without hanging or crashing.
* **Dynamic Intent Classification:** Scans incoming raw keyboard strings on-the-fly. If the user input contains trigger keywords like `"mess"` or `"dirty"`, it instantly abstracts the data into an urgent `HouseworkAlert` record with pre-assigned mess levels and target room tags. All other generic strings are routed safely into standard `ConversationalChat` containers.
* **Exception Isolation Boundaries:** Wraps the engine execution loop inside a global `try-catch` boundary.
* If a network disruption or cloud timeout occurs, it safely intercepts the failure, reports the exact HTTP error signature,
* and keeps the terminal session active instead of allowing the app to crash out.

---

## ⚡ Getting Started & Installation

### Prerequisites
* **Java Development Kit (JDK) 26** or newer installed on your machine.
* A free developer API key from the **[Groq Console](https://groq.com)**.

### Configuration
1. Open `StacyEngine.java` in your code editor.
2. Locate the line containing `private static final String GROQ_API_KEY = "YOUR_GSK_KEY_HERE";`.
3. Paste your Groq API key (starts with `gsk_`) directly inside the quotation marks and save the file.

### Execution
Because this platform implements preview features of the latest Java ecosystem, 
you must pass the preview flags to both the compiler and the launcher.
Open your terminal inside the project directory and run:

```bash
# 1. Compile the source files with JDK 26 preview mode enabled
javac --enable-preview --release 26 StacyEngine.java StacyApp.java

# 2. Launch the interactive CLI application
java --enable-preview StacyApp
```

---
If you are showcasing this project on your resume, use these metrics-focused engineering bullets:
* **Architected an adaptive conversational AI platform** utilizing **JDK 26 preview features**,
* implementing an isolated, low-latency background orchestration layer to bypass slow monolithic framework dependencies.
* **Optimized thread execution and pipeline latency** by integrating **Structured Concurrency (JEP 525)**
* via `StructuredTaskScope`, splitting contextual state discovery and user-profile queries into asynchronous, parallel subtasks.
* **Eliminated runtime casting vulnerabilities** by designing type-safe interaction routers using modern **Sealed Interfaces** and record pattern switches to filter application intents cleanly.
* **Built a zero-dependency network networking stack** leveraging Java’s native `HttpClient` to format, sanitize, and securely stream structured high-density JSON payloads directly to remote cloud-inferencing LLM APIs.
