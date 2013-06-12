import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.core.PollableChannel;
import org.springframework.integration.message.GenericMessage;

public class SpikeMain {

	public static void main(String[] args) {
		AbstractApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/spring-context.xml", SpikeMain.class);
		MessageChannel fooChannel = context.getBean("foo", MessageChannel.class);
		PollableChannel quuChannel = context.getBean("quu", PollableChannel.class);
		
		fooChannel.send(new GenericMessage<String>("blah"));
		System.out.println("got: " + quuChannel.receive(0).getPayload());
	}
}
