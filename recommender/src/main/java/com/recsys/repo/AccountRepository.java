package com.recsys.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.recsys.model.Account;

public interface AccountRepository extends JpaRepository<Account,Integer>{
	
	Account findById(int id);
	
	Account findByEmail(String email);
	
	List<Account> findByLastName(String lastName);
	
}