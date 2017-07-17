package com.app.sample.fchat.model;

import com.app.sample.fchat.data.ParseFirebaseData;

import java.io.Serializable;

public class Friend implements Serializable {
	private String id;
	private String name;
	private String photo;

	public Friend(String id, String name, String photo) {
		this.id = id;
		this.name = name;
		this.photo = photo;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhoto() {
		return photo;
	}
}
