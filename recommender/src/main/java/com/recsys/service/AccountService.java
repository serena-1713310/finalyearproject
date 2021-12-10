package com.recsys.service;

import com.recsys.model.Account;

public interface AccountService {
	
	void register(final Account account);
	
	boolean checkUserExists(final String email);
	
	Account getById(final int id);
}
