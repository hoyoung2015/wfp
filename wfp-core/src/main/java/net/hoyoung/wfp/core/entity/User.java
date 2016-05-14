package net.hoyoung.wfp.core.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="user")
public class User {
	@Id
	private String id;
	private String name;
//	private Address address;
//	
//	public Address getAddress() {
//		return address;
//	}
//	public void setAddress(Address address) {
//		this.address = address;
//	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	class Address{
//		private String street;
//		private String no;
//		public String getStreet() {
//			return street;
//		}
//		public void setStreet(String street) {
//			this.street = street;
//		}
//		public String getNo() {
//			return no;
//		}
//		public void setNo(String no) {
//			this.no = no;
//		}
//	}
}
