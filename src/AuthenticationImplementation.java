import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.Date;

public class AuthenticationImplementation extends UnicastRemoteObject implements AuthenticationInterface {
	AuthenticationImplementation() throws Exception{
	}
	@Override
	public boolean userAuth(String username, String password){
		boolean isLogin = true;
		if(username.equals("User1") && password.equals("1234")){
			return isLogin;
		}else if(username.equals("User2") && password.equals("1234")){
			return isLogin;
		}else if(username.equals("Jack") && password.equals("1234")){
			return isLogin;
		}else if(username.equals("Tristan") && password.equals("1234")){
			return isLogin;
		}else {
			return false;
		}

	}
}
