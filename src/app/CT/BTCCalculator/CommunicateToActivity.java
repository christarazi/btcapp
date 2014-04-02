package app.CT.BTCCalculator;

/**
 * @author lt_tibs
 *
 */
public interface CommunicateToActivity {

	/**
	 * 
	 * @param message to send to the Parent Activity
	 */
	public abstract void SendMessageToParent(String message);
}
