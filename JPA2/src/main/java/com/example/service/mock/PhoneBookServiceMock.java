package com.example.service.mock;

import java.util.List;

import javax.enterprise.inject.Alternative;

import com.example.entity.Person;
import com.example.entity.PhoneNumber;
import com.example.service.interfaces.PhoneBookService;

@Alternative
public class PhoneBookServiceMock implements PhoneBookService {

	@Override
	public Person getPerson(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Person> getAllPersons() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Person> getPersonsByLastName(String lastname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPerson(Person person) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deletePerson(Person person) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updatePerson(Person person) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deletePerson(int personId) {
		// TODO Auto-generated method stub
	}

	@Override
    public void addPhoneNumber(PhoneNumber phoneNumber) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void deletePhoneNumber(PhoneNumber phoneNumber) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void deletePhoneNumber(int phoneNumberId) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void updatePersons(List<Person> persons) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public List<Person> searchWithPhoneNumberLike(String number, SearchType searchType) {
	    // TODO Auto-generated method stub
	    return null;
    }

}
