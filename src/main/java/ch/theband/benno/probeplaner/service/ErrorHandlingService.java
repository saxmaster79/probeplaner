package ch.theband.benno.probeplaner.service;

import javafx.concurrent.Service;

public abstract class ErrorHandlingService<T> extends Service<T> {

	public ErrorHandlingService() {
		setOnFailed(x -> {
			throw new RuntimeException(x.getSource().getException());
		});
	}

}
