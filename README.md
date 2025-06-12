# RabbitMQ implementation with producer and consumer
In this repository we have a simple implementation of RabbitMQ with a producer and consumer.
- Producer: this is a kotlin project build with Gradle, you can find it in the directory `messaging`.
- Consumer: this is a python project, you can find it in the directory `messageReceiver`.

## Description
The Message Queue is a integration protocol that allows you to send messages between applications. In this case, we are using RabbitMQ as the message system. It connects with both apps using the AMQP protocol. The producer sends messages to a queue, and the consumer receives messages from that queue.

### Here are the screenshots of the apps:
#### Consolse with the producer and consumer running
![Producer](src/producer_consumer.png)

On the left side you can see the kotlin producer,, it send a message with the number of the message and the time it was sent. On the right side you can see the python consumer, it receives the message and prints it on the console.

#### RabbitMQ console
![RabbitMQ Console](src/rabbit_console1.png)
![RabbitMQ Console](src/rabbit_console2.png)

## Decoupling
The way this practice is implemented allows the producer and consumer to be decoupled. This means that the producer can send messages without knowing who will consume them, and the consumer can receive messages without knowing who sent them. This allows for a more flexible architecture, where you can add or remove producers and consumers without affecting the other components.
## Advantages
The main advantages I notice in the implementation of RabbitMQ in the two projects are:
- **Asynchronous communication**: The producer can send messages without waiting for the consumer to process them, I even turn off the consumer and then turn it back on and no messages were lost.
- **Language agnostic**: The producer and consumer can be implemented in different programming languages, as long as they use the AMQP protocol.

## How To Run

---

### Prerequisites

*   A running RabbitMQ instance.
*   Python 3.8+ and `pip`.
*   JDK 8+ (Java Development Kit).
*   The project includes a Gradle wrapper, so you do not need to install Gradle separately.

### ⚙️ Configuration (Required for Both)

Both applications require a connection string to RabbitMQ.

1.  Create a file named `.env` in the root directory of **both** the Python and Kotlin projects.
2.  Add your AMQP connection URL to the file. **Remember to include your specific vhost at the end.**

    ```env
    # .env
    AMQP_URL="amqp://YOUR_USER:YOUR_PASSWORD@YOUR_HOST:5672/YOUR_VHOST"
    ```

---

### 🐍 Running the Python Consumer

The consumer listens for messages on the `integracion` queue.

1.  **Navigate to the Python project directory:**
    ```bash
    cd path/to/your-python-consumer
    ```

2.  **Create and activate a virtual environment:**
    ```bash
    python3 -m venv .venv
    source .venv/bin/activate
    ```

3.  **Install dependencies:**
    Create a `requirements.txt` file with the following content:
    ```txt
    # requirements.txt
    pika==1.3.2
    python-dotenv==1.0.1
    ```
    Then run the installation command:
    ```bash
    pip install -r requirements.txt
    ```

4.  **Run the consumer:**
    ```bash
    python consumer.py
    ```

    You should see the following output as it waits for messages:
    ```
    [*] Successfully connected! Waiting for messages in queue "integracion". To exit press CTRL+C
    ```

---
### 🐘 Running the Kotlin Producer

The producer sends a new message every 5 seconds.

1.  **Navigate to the Kotlin project directory:**
    ```bash
    cd path/to/your-kotlin-producer
    ```

2.  **Load the environment variable:**
    Before running, you must load the `.env` file into your shell session.
    ```bash
    source .env
    ```

3.  **Run the producer using the Gradle wrapper:**
    This command will compile and run the application.
    ```bash
    ./gradlew run
    ```

    You should see the following output as it sends messages:
    ```
    [✔] Successfully connected! Starting producer loop...
     [✔] Sent: 'Message #1 from Kotlin producer at 2025-06-11T21:15:00.123'
     [✔] Sent: 'Message #2 from Kotlin producer at 2025-06-11T21:15:05.128'
    ```

With both applications running, you will see the messages sent by the Kotlin producer appear in the terminal of the Python consumer.
