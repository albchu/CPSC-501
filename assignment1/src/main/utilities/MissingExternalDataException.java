package main.utilities;

@SuppressWarnings("serial")
public class MissingExternalDataException extends RuntimeException {
	
	public MissingExternalDataException(Throwable e){
		super(e);
	}
	
	public MissingExternalDataException(String message) {
		super(message);
	}
	
	public MissingExternalDataException(String message, Throwable e){
		super(message, e);
	}

}
