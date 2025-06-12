# consumer.py
import pika
import sys
import os
import time
from dotenv import load_dotenv


def main():
    """
    Connects to RabbitMQ and consumes messages from the 'integracion' queue.
    """
    load_dotenv()

    # MODIFIED: Add .strip() to clean the URL of any hidden whitespace/newlines
    amqp_url = os.environ.get("AMQP_URL", "").strip()

    # This print statement is great for debugging, let's keep it for now
    print(f"Attempting to connect with URL: {amqp_url}")

    if not amqp_url:
        print("Error: AMQP_URL environment variable not set or is empty.")
        print('Please create a .env file with AMQP_URL="amqp://..."')
        sys.exit(1)

    try:
        params = pika.URLParameters(amqp_url)
        connection = pika.BlockingConnection(params)
        channel = connection.channel()

        channel.queue_declare(queue="integracion", durable=True)
        print(
            ' [*] Successfully connected! Waiting for messages in queue "integracion". To exit press CTRL+C'
        )

        def callback(ch, method, properties, body):
            message = body.decode("utf-8")
            print(f" [x] Received message: '{message}'")
            time.sleep(1)
            print(" [✔] Done processing.")
            ch.basic_ack(delivery_tag=method.delivery_tag)

        channel.basic_qos(prefetch_count=1)
        channel.basic_consume(
            queue="integracion", on_message_callback=callback
        )

        channel.start_consuming()

    except Exception as e:
        # Catching a broader exception class can be helpful for debugging
        print(f"Error: Could not connect to RabbitMQ.")
        print(f"Please check your AMQP_URL and ensure the server is accessible.")
        print(f"Details: {e}")
        # Also print the type of exception
        print(f"Exception Type: {type(e).__name__}")
    except KeyboardInterrupt:
        print("\n[!] Interrupted by user. Stopping consumer.")
        if "connection" in locals() and connection.is_open:
            connection.close()
        sys.exit(0)


if __name__ == "__main__":
    main()
