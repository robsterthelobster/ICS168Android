import java.io.*;

@SuppressWarnings("serial")
public final class Message implements Serializable {
	public int number;
	Message(int number) {
		this.number = number;
	}
}