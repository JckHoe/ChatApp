import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthenticationInterface extends Remote{
	public boolean userAuth(String username, String password) throws RemoteException;
}
