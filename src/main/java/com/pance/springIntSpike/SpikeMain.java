import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.DeliveryMode;
import javax.jms.TextMessage;
import javax.jms.Message;

public class SpikeMain {

	public static void main(String[] args) {
		AbstractApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/spring-context.xml", SpikeMain.class);
		
		Thread sendJMS, receiveJMS;
		
		sendJMS = new Thread(new Runnable() {
			public void run() {
				System.out.println("\n******sendJMS: preparing...");
				try {
					ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost");
					Connection connection = cf.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue("spike.receive");

					MessageProducer producer = session.createProducer(destination);
					producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

					String msg = "HELLO, THIS IS A TEST.";
					TextMessage message = session.createTextMessage(msg);

					producer.send(message);
					System.out.println("\n******sendJMS: sent message!");

					session.close();
					connection.close();
				} catch (Exception e) {
					System.out.println("Exception caught in sendJMS");
				}
			}
		}); // end of sendJMS anon Runnable class
		receiveJMS = new Thread(new Runnable() {
			public void run() {
				try {
					ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost");
					Connection connection = cf.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue("spike.produce");

					MessageConsumer consumer = session.createConsumer(destination);

					System.out.println("\n******receiveJMS: reading message...");
					Message message = consumer.receive(1000);

					System.out.println("\n******receiveJMS: got message!");

					if(message instanceof TextMessage) {
						TextMessage textMesssage = (TextMessage) message;
						System.out.println("\n******receiveJMS: message: " + textMesssage.getText());
					} else {
						System.out.println("\n******receiveJMS: not a text message: " + message);
					}
						consumer.close();
						session.close();
						connection.close();
				} catch (Exception e) {
					System.out.println("Exception caught in receiveJMS");
				}
			}
		}); //end of receiveJMS anon Runnable class
		sendJMS.start();
		receiveJMS.start();
		try {
			sendJMS.join();
			receiveJMS.join();
		} catch (Exception e) { }
		System.out.println("Closing Spring app context...");
		context.close();
	}
}
