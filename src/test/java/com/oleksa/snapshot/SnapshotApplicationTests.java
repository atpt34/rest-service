package com.oleksa.snapshot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oleksa.snapshot.entity.Room;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class SnapshotApplicationTests {

	private final ObjectMapper objectMapper = new ObjectMapper();

	
	void contextLoads() {
	}

	
	void testObjectMapper() throws JsonProcessingException {
		Room room = Room.builder()
				.id(UUID.randomUUID())
				.location("some loc")
				.name("blah room #1")
				.build();

		String s = objectMapper.writeValueAsString(room);
		System.out.println(s);
	}

	
	void readString() throws JsonProcessingException {
		Room room = objectMapper.readValue("{\"id\":\"84783bca-ab7c-4386-9c4f-87bc7b2ddb5f\",\"name\":\"blah room #1\",\"location\":\"some loc\"}", Room.class);
		System.out.println(room);
	}

//	public static class WrapperAbc<T> {
//		List<T> elems = new ArrayList<>();
//
//		public void setElems(List<T> elems) {
//			this.elems = elems;
//		}
//
//		public List<T> getElems() {
//			return elems;
//		}
//	}

	public static class WrapperAbcSecond {
		List<Abc> elems = new ArrayList<>();

		public void setElems(List<Abc> elems) {
			this.elems = elems;
		}

		public List<Abc> getElems() {
			return elems;
		}
	}

	@AllArgsConstructor
	@NoArgsConstructor(force = true)
	@Value
	public static class Abc {
		private UUID id;
		private String name;

	}

	
	public void testWrapper() throws JsonProcessingException {
//		WrapperAbc<Abc> abcWrapperAbc = new WrapperAbc<>();
		WrapperAbcSecond abcWrapperAbc = new WrapperAbcSecond();
		abcWrapperAbc.setElems(new ArrayList<>(List.of(
				new Abc(UUID.randomUUID(), "one"),
				new Abc(UUID.randomUUID(), "two"),
				new Abc(UUID.randomUUID(), "three")
		)));
		System.out.println(abcWrapperAbc);
//
		String value = objectMapper.writeValueAsString(abcWrapperAbc);

//		System.out.println(value);
//		WrapperAbc wrapperAbcRead = objectMapper.readValue(value, WrapperAbc.class);

		System.out.println(value);
		WrapperAbcSecond wrapperAbcRead = objectMapper.readValue(value, WrapperAbcSecond.class);

//		String valuePrev = "{\"elems\":" +
//				"[{\"id\":\"fc8495f6-c9d8-436a-b99b-9fca04c87752\",\"name\":\"one\"}," +
//				"{\"id\":\"4c3c5f06-0232-41c3-b9be-109a313d7859\",\"name\":\"two\"}," +
//				"{\"id\":\"d8fe393b-f23b-465b-84b0-2bf0600b8379\",\"name\":\"three\"}]}";
//		WrapperAbcSecond wrapperAbcRead = objectMapper.readValue(valuePrev, WrapperAbcSecond.class);
//
//		System.out.println(wrapperAbcRead);

		List elems = wrapperAbcRead.getElems();
		System.out.println(elems);
		System.out.println(elems.get(0));
		System.out.println(elems.get(1));
		System.out.println(elems.get(2));


	}
}
