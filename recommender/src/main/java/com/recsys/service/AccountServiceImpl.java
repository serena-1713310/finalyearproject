package com.recsys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.recsys.model.Account;
import com.recsys.repo.AccountRepository;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountService accountService;
	
	@Autowired
    private AccountRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
	@Override
	public void register(Account account) {
		String email = account.getEmail();
		if(checkUserExists(email)) {
			System.out.println(String.format("User already exists for %s",email));
		}
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		userRepository.save(account);
	}

	@Override
	public boolean checkUserExists(String email) {
		return userRepository.findByEmail(email) != null ? true : false;
	}

	@Override
	public Account getById(int id) {
		Account account = userRepository.findById(id);
		if(account == null) {
			System.out.println("Unable to find this user");
		}
		return account;
	}

}
