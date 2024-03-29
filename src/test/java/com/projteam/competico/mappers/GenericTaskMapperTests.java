package com.projteam.competico.mappers;

import static com.projteam.competico.testutils.ListAssert.assertListContentEquals;
import static com.projteam.competico.testutils.ListAssert.assertListContentMatches;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import com.projteam.competico.domain.game.tasks.ChoiceWordFill;
import com.projteam.competico.domain.game.tasks.ChoiceWordFillElement;
import com.projteam.competico.domain.game.tasks.ChronologicalOrder;
import com.projteam.competico.domain.game.tasks.ListChoiceWordFill;
import com.projteam.competico.domain.game.tasks.ListSentenceForming;
import com.projteam.competico.domain.game.tasks.ListWordFill;
import com.projteam.competico.domain.game.tasks.OptionSelect;
import com.projteam.competico.domain.game.tasks.OptionSelectElement;
import com.projteam.competico.domain.game.tasks.SentenceFormingElement;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.domain.game.tasks.WordConnect;
import com.projteam.competico.domain.game.tasks.WordFill;
import com.projteam.competico.domain.game.tasks.WordFillElement;
import com.projteam.competico.domain.game.tasks.ChoiceWordFillElement.WordChoice;
import com.projteam.competico.domain.game.tasks.WordFillElement.EmptySpace;
import com.projteam.competico.dto.game.tasks.create.ChoiceWordFillDTO;
import com.projteam.competico.dto.game.tasks.create.ChoiceWordFillElementDTO;
import com.projteam.competico.dto.game.tasks.create.ChronologicalOrderDTO;
import com.projteam.competico.dto.game.tasks.create.ListChoiceWordFillDTO;
import com.projteam.competico.dto.game.tasks.create.ListSentenceFormingDTO;
import com.projteam.competico.dto.game.tasks.create.ListWordFillDTO;
import com.projteam.competico.dto.game.tasks.create.OptionSelectDTO;
import com.projteam.competico.dto.game.tasks.create.OptionSelectElementDTO;
import com.projteam.competico.dto.game.tasks.create.SentenceFormingElementDTO;
import com.projteam.competico.dto.game.tasks.create.TaskDTO;
import com.projteam.competico.dto.game.tasks.create.WordConnectDTO;
import com.projteam.competico.dto.game.tasks.create.WordFillDTO;
import com.projteam.competico.dto.game.tasks.create.WordFillElementDTO;
import com.projteam.competico.dto.game.tasks.create.ChoiceWordFillElementDTO.WordChoiceDTO;
import com.projteam.competico.dto.game.tasks.create.WordFillElementDTO.EmptySpaceDTO;
import com.projteam.competico.service.game.tasks.mappers.GenericTaskMapper;

@SpringBootTest(webEnvironment = NONE)
@ContextConfiguration(name = "Service-tests")
class GenericTaskMapperTests
{
	@Autowired
	private GenericTaskMapper mapper;
	
	@Test
	public void canMapWordFillToDTO()
	{
		WordFill entity = mockWordFill();
		WordFillDTO dto = (WordFillDTO) mapper.toDTO(entity);
		
		assertNotNull(dto);
		assertEquals(dto.getInstruction(), entity.getInstruction());
		assertEquals(dto.getTags(), entity.getTags());
		assertEquals(dto.getDifficulty(), entity.getDifficulty());
		
		assertEquals(dto.getContent().getText(), entity.getContent().getText());
		assertListContentEquals(
				dto.getContent().getEmptySpaces(),
				entity.getContent().getEmptySpaces(),
				dtoES -> dtoES.getAnswer(),
				entES -> entES.getAnswer());
		assertEquals(dto.getContent().getPossibleAnswers(), entity.getContent().getPossibleAnswers());
		assertEquals(dto.getContent().isStartWithText(), entity.getContent().isStartWithText());
	}
	@Test
	public void canMapListWordFillToDTO()
	{
		ListWordFill entity = mockListWordFill();
		ListWordFillDTO dto = (ListWordFillDTO) mapper.toDTO(entity);
		
		assertNotNull(dto);
		assertEquals(dto.getInstruction(), entity.getInstruction());
		assertEquals(dto.getTags(), entity.getTags());
		assertEquals(dto.getDifficulty(), entity.getDifficulty());
		
		assertListContentMatches(
				dto.getRows(),
				entity.getRows(),
				(dtoRow, entRow) ->
				{
					assertEquals(dtoRow.getText(), entRow.getText());
					assertListContentEquals(
							dtoRow.getEmptySpaces(),
							entRow.getEmptySpaces(),
							dtoES -> dtoES.getAnswer(),
							entES -> entES.getAnswer());
					assertEquals(dtoRow.getPossibleAnswers(), entRow.getPossibleAnswers());
					assertEquals(dtoRow.isStartWithText(), entRow.isStartWithText());
				});
	}
	@Test
	public void canMapChoiceWordFillToDTO()
	{
		ChoiceWordFill entity = mockChoiceWordFill();
		ChoiceWordFillDTO dto = (ChoiceWordFillDTO) mapper.toDTO(entity);
		
		assertNotNull(dto);
		assertEquals(dto.getInstruction(), entity.getInstruction());
		assertEquals(dto.getTags(), entity.getTags());
		assertEquals(dto.getDifficulty(), entity.getDifficulty());
		
		assertEquals(dto.getContent().getText(), entity.getContent().getText());
		assertListContentMatches(
				dto.getContent().getWordChoices(),
				entity.getContent().getWordChoices(),
				(dtoWC, entWC) ->
				{
					assertEquals(dtoWC.getCorrectAnswer(),
							entWC.getCorrectAnswer());
					assertEquals(dtoWC.getIncorrectAnswers(),
							entWC.getIncorrectAnswers());
				});
		assertEquals(dto.getContent().isStartWithText(), entity.getContent().isStartWithText());
	}
	@Test
	public void canMapListChoiceWordFillToDTO()
	{
		ListChoiceWordFill entity = mockListChoiceWordFill();
		ListChoiceWordFillDTO dto = (ListChoiceWordFillDTO) mapper.toDTO(entity);
		
		assertNotNull(dto);
		assertEquals(dto.getInstruction(), entity.getInstruction());
		assertEquals(dto.getTags(), entity.getTags());
		assertEquals(dto.getDifficulty(), entity.getDifficulty());
		
		assertListContentMatches(
				dto.getRows(),
				entity.getRows(),
				(dtoRow, entRow) ->
				{
					assertEquals(dtoRow.getText(), entRow.getText());
					assertListContentMatches(
							dtoRow.getWordChoices(),
							entRow.getWordChoices(),
							(dtoWC, entWC) ->
							{
								assertEquals(dtoWC.getCorrectAnswer(),
										entWC.getCorrectAnswer());
								assertEquals(dtoWC.getIncorrectAnswers(),
										entWC.getIncorrectAnswers());
							});
					assertEquals(dtoRow.isStartWithText(), entRow.isStartWithText());
				});
	}
	@Test
	public void canMapListSentenceFormingToDTO()
	{
		ListSentenceForming entity = mockListSentenceForming();
		ListSentenceFormingDTO dto = (ListSentenceFormingDTO) mapper.toDTO(entity);
		
		assertNotNull(dto);
		assertEquals(dto.getInstruction(), entity.getInstruction());
		assertEquals(dto.getTags(), entity.getTags());
		assertEquals(dto.getDifficulty(), entity.getDifficulty());
		
		assertListContentEquals(
				dto.getRows(),
				entity.getRows(),
				dtoRow -> dtoRow.getWords(),
				entityRow -> entityRow.getWords());
	}
	@Test
	public void canMapWordConnectToDTO()
	{
		WordConnect entity = mockWordConnect();
		WordConnectDTO dto = (WordConnectDTO) mapper.toDTO(entity);
		
		assertNotNull(dto);
		assertEquals(dto.getInstruction(), entity.getInstruction());
		assertEquals(dto.getTags(), entity.getTags());
		assertEquals(dto.getDifficulty(), entity.getDifficulty());
		
		assertEquals(dto.getLeftWords(), entity.getLeftWords());
		assertEquals(dto.getRightWords(), entity.getRightWords());
		assertEquals(dto.getCorrectMapping(), entity.getCorrectMapping());
	}
	@Test
	public void canMapChronologicalOrderToDTO()
	{
		ChronologicalOrder entity = mockChronologicalOrder();
		ChronologicalOrderDTO dto = (ChronologicalOrderDTO) mapper.toDTO(entity);
		
		assertNotNull(dto);
		assertEquals(dto.getInstruction(), entity.getInstruction());
		assertEquals(dto.getTags(), entity.getTags());
		assertEquals(dto.getDifficulty(), entity.getDifficulty());
		
		assertEquals(dto.getSentences(), entity.getSentences());
	}
	@Test
	public void canMapOptionSelectToDTO()
	{
		OptionSelect entity = mockOptionSelect();
		OptionSelectDTO dto = (OptionSelectDTO) mapper.toDTO(entity);
		
		assertNotNull(dto);
		assertEquals(dto.getInstruction(), entity.getInstruction());
		assertEquals(dto.getTags(), entity.getTags());
		assertEquals(dto.getDifficulty(), entity.getDifficulty());
		
		assertEquals(dto.getContent().getContent(), entity.getContent().getContent());
		assertEquals(dto.getContent().getCorrectAnswers(), entity.getContent().getCorrectAnswers());
		assertEquals(dto.getContent().getIncorrectAnswers(), entity.getContent().getIncorrectAnswers());
	}
	
	@Test
	public void canMapWordFillToEntity()
	{
		WordFillDTO dto = mockWordFillDTO();
		WordFill entity = (WordFill) mapper.toEntity(dto);
		
		assertNotNull(entity);
		assertEquals(entity.getInstruction(), dto.getInstruction());
		assertEquals(entity.getTags(), dto.getTags());
		assertEquals(entity.getDifficulty(), dto.getDifficulty());
		
		assertNotNull(entity.getId());
		assertNotNull(entity.getContent().getId());
		
		assertEquals(entity.getContent().getText(), dto.getContent().getText());
		assertListContentEquals(
				entity.getContent().getEmptySpaces(),
				dto.getContent().getEmptySpaces(),
				dtoES -> dtoES.getAnswer(),
				entES -> entES.getAnswer());
		assertEquals(entity.getContent().getPossibleAnswers(), dto.getContent().getPossibleAnswers());
		assertEquals(entity.getContent().isStartWithText(), dto.getContent().isStartWithText());
	}
	@Test
	public void canMapListWordFillToEntity()
	{
		ListWordFillDTO dto = mockListWordFillDTO();
		ListWordFill entity = (ListWordFill) mapper.toEntity(dto);
		
		assertNotNull(entity);
		assertEquals(entity.getInstruction(), dto.getInstruction());
		assertEquals(entity.getTags(), dto.getTags());
		assertEquals(entity.getDifficulty(), dto.getDifficulty());
		
		assertNotNull(entity.getId());
		assertListContentMatches(
				entity.getRows(),
				dto.getRows(),
				(entRow, dtoRow) ->
				{
					assertNotNull(entRow.getId());
					assertEquals(dtoRow.getText(), entRow.getText());
					assertListContentEquals(
							dtoRow.getEmptySpaces(),
							entRow.getEmptySpaces(),
							dtoES -> dtoES.getAnswer(),
							entES -> entES.getAnswer());
					assertEquals(dtoRow.getPossibleAnswers(), entRow.getPossibleAnswers());
					assertEquals(dtoRow.isStartWithText(), entRow.isStartWithText());
				});
	}
	@Test
	public void canMapChoiceWordFillToEntity()
	{
		ChoiceWordFillDTO dto = mockChoiceWordFillDTO();
		ChoiceWordFill entity = (ChoiceWordFill) mapper.toEntity(dto);
		
		assertNotNull(entity);
		assertEquals(entity.getInstruction(), dto.getInstruction());
		assertEquals(entity.getTags(), dto.getTags());
		assertEquals(entity.getDifficulty(), dto.getDifficulty());
		
		assertNotNull(entity.getId());
		assertNotNull(entity.getContent().getId());
		assertEquals(entity.getContent().getText(), dto.getContent().getText());
		assertListContentMatches(
				entity.getContent().getWordChoices(),
				dto.getContent().getWordChoices(),
				(entWC, dtoWC) ->
				{
					assertNotNull(entWC.getId());
					assertEquals(dtoWC.getCorrectAnswer(),
							entWC.getCorrectAnswer());
					assertEquals(dtoWC.getIncorrectAnswers(),
							entWC.getIncorrectAnswers());
				});
		assertEquals(entity.getContent().isStartWithText(), dto.getContent().isStartWithText());
	}
	@Test
	public void canMapListChoiceWordFillToEntity()
	{
		ListChoiceWordFillDTO dto = mockListChoiceWordFillDTO();
		ListChoiceWordFill entity = (ListChoiceWordFill) mapper.toEntity(dto);
		
		assertNotNull(entity);
		assertEquals(entity.getInstruction(), dto.getInstruction());
		assertEquals(entity.getTags(), dto.getTags());
		assertEquals(entity.getDifficulty(), dto.getDifficulty());
		
		assertNotNull(entity.getId());
		assertListContentMatches(
				entity.getRows(),
				dto.getRows(),
				(entRow, dtoRow) ->
				{
					assertNotNull(entRow.getId());
					assertEquals(dtoRow.getText(), entRow.getText());
					assertListContentMatches(
							dtoRow.getWordChoices(),
							entRow.getWordChoices(),
							(dtoWC, entWC) ->
							{
								assertNotNull(entWC.getId());
								assertEquals(dtoWC.getCorrectAnswer(),
										entWC.getCorrectAnswer());
								assertEquals(dtoWC.getIncorrectAnswers(),
										entWC.getIncorrectAnswers());
							});
					assertEquals(dtoRow.isStartWithText(), entRow.isStartWithText());
				});
	}
	@Test
	public void canMapListSentenceFormingToEntity()
	{
		ListSentenceFormingDTO dto = mockListSentenceFormingDTO();
		ListSentenceForming entity = (ListSentenceForming) mapper.toEntity(dto);
		
		assertNotNull(entity);
		assertEquals(entity.getInstruction(), dto.getInstruction());
		assertEquals(entity.getTags(), dto.getTags());
		assertEquals(entity.getDifficulty(), dto.getDifficulty());

		assertNotNull(entity.getId());
		assertListContentMatches(
				entity.getRows(),
				dto.getRows(),
				(entityRow, dtoRow) -> 
				{
					assertNotNull(entityRow.getId());
					assertEquals(entityRow.getWords(), dtoRow.getWords());
				});
	}
	@Test
	public void canMapWordConnectToEntity()
	{
		WordConnectDTO dto = mockWordConnectDTO();
		WordConnect entity = (WordConnect) mapper.toEntity(dto);
		
		assertNotNull(entity);
		assertEquals(entity.getInstruction(), dto.getInstruction());
		assertEquals(entity.getTags(), dto.getTags());
		assertEquals(entity.getDifficulty(), dto.getDifficulty());
		
		assertNotNull(entity.getId());
		assertEquals(entity.getLeftWords(), dto.getLeftWords());
		assertEquals(entity.getRightWords(), dto.getRightWords());
		assertEquals(entity.getCorrectMapping(), dto.getCorrectMapping());
	}
	@Test
	public void canMapChronologicalOrderToEntity()
	{
		ChronologicalOrderDTO dto = mockChronologicalOrderDTO();
		ChronologicalOrder entity = (ChronologicalOrder) mapper.toEntity(dto);
		
		assertNotNull(entity);
		assertEquals(entity.getInstruction(), dto.getInstruction());
		assertEquals(entity.getTags(), dto.getTags());
		assertEquals(entity.getDifficulty(), dto.getDifficulty());
		
		assertNotNull(entity.getId());
		assertEquals(entity.getSentences(), dto.getSentences());
	}
	@Test
	public void canMapOptionSelectToEntity()
	{
		OptionSelectDTO dto = mockOptionSelectDTO();
		OptionSelect entity = (OptionSelect) mapper.toEntity(dto);
		
		assertNotNull(entity);
		assertEquals(entity.getInstruction(), dto.getInstruction());
		assertEquals(entity.getTags(), dto.getTags());
		assertEquals(entity.getDifficulty(), dto.getDifficulty());
		
		assertNotNull(entity.getId());
		assertNotNull(entity.getContent().getId());
		assertEquals(entity.getContent().getContent(), dto.getContent().getContent());
		assertEquals(entity.getContent().getCorrectAnswers(), dto.getContent().getCorrectAnswers());
		assertEquals(entity.getContent().getIncorrectAnswers(), dto.getContent().getIncorrectAnswers());
	}
	
	@Test
	public void shouldThrowWhenNoMapperCanAcceptDTO()
	{
		TaskDTO mockDTO = mock(TaskDTO.class);
		assertThrows(IllegalArgumentException.class, () -> mapper.toEntity(mockDTO));
	}
	@Test
	public void shouldThrowWhenNoMapperCanAcceptEntity()
	{
		Task mockEntity = mock(Task.class);
		assertThrows(IllegalArgumentException.class, () -> mapper.toDTO(mockEntity));
	}

	//---Helpers---
	
	public static WordFill mockWordFill()
	{
		List<String> wfText = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> wfAnswers = List.of("abc", "def", "ghi", "jkl");
		List<EmptySpace> wfEmptySpaces = wfAnswers
				.stream()
				.map(ans -> new EmptySpace(ans))
				.collect(Collectors.toList());
		List<String> wfPossibleAnswers = List.of("abc", "def", "ghi", "jkl", "mno", "pqr");
		
		return new WordFill(UUID.randomUUID(),
				"Test instruction", List.of(),
				new WordFillElement(UUID.randomUUID(),
						wfText, wfEmptySpaces, false, wfPossibleAnswers), 100);
	}
	public static WordFillDTO mockWordFillDTO()
	{
		List<String> wfText = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> wfAnswers = List.of("abc", "def", "ghi", "jkl");
		List<EmptySpaceDTO> wfEmptySpaces = wfAnswers
				.stream()
				.map(ans -> new EmptySpaceDTO(ans))
				.collect(Collectors.toList());
		List<String> wfPossibleAnswers = List.of("abc", "def", "ghi", "jkl", "mno", "pqr");
		
		return new WordFillDTO("Test instruction", List.of(), 100,
				new WordFillElementDTO(wfText, wfEmptySpaces, false, wfPossibleAnswers));
	}
	public static ChoiceWordFill mockChoiceWordFill()
	{
		List<String> cwfText = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> cwfAnswers = List.of("abc", "def", "ghi", "jkl");
		List<WordChoice> wordChoices = cwfAnswers
				.stream()
				.map(ans -> new WordChoice(UUID.randomUUID(), ans, List.of("qwr")))
				.collect(Collectors.toList());
		
		return new ChoiceWordFill(UUID.randomUUID(),
				"Test instruction", List.of(),
				new ChoiceWordFillElement(UUID.randomUUID(),
						cwfText, wordChoices, false), 100);
	}
	public static ChoiceWordFillDTO mockChoiceWordFillDTO()
	{
		List<String> cwfText = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> cwfAnswers = List.of("abc", "def", "ghi", "jkl");
		List<WordChoiceDTO> wordChoices = cwfAnswers
				.stream()
				.map(ans -> new WordChoiceDTO(ans, List.of("qwr")))
				.collect(Collectors.toList());
		
		return new ChoiceWordFillDTO(
				"Test instruction", List.of(), 100,
				new ChoiceWordFillElementDTO(
						cwfText, wordChoices, false));
	}
	public static ListWordFill mockListWordFill()
	{
		List<List<String>> lwfText = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		List<List<String>> lwfAnswers = List.of(
				List.of("abc", "def"),
				List.of("ghi"),
				List.of("jkl"),
				List.of("mno"));
		List<List<String>> lwfPossibleAnswers = List.of(
				List.of("abc", "def", "ghi", "jkl"),
				List.of("ghi", "def", "ghi"),
				List.of("jkl", "ghi", "jkl"),
				List.of("mno", "ghi", "def"));
		
		List<WordFillElement> lwfWordFillElemList = new ArrayList<>();
		Iterator<List<String>> lwfTextIter = lwfText.iterator();
		Iterator<List<String>> lwfPossAnsIter = lwfPossibleAnswers.iterator();
		for (List<String> answerList: lwfAnswers)
		{
			List<String> textList = lwfTextIter.next();
			List<String> possibleAnswersList = lwfPossAnsIter.next();
			lwfWordFillElemList.add(new WordFillElement(UUID.randomUUID(),
					textList,
					answerList.stream()
						.map(ans -> new EmptySpace(ans))
						.collect(Collectors.toList()),
					true,
					possibleAnswersList));
		}
		
		return new ListWordFill(UUID.randomUUID(),
				"Test instruction", List.of(),
				lwfWordFillElemList, 100);
	}
	public static ListWordFillDTO mockListWordFillDTO()
	{
		List<List<String>> lwfText = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		List<List<String>> lwfAnswers = List.of(
				List.of("abc", "def"),
				List.of("ghi"),
				List.of("jkl"),
				List.of("mno"));
		List<List<String>> lwfPossibleAnswers = List.of(
				List.of("abc", "def", "ghi", "jkl"),
				List.of("ghi", "def", "ghi"),
				List.of("jkl", "ghi", "jkl"),
				List.of("mno", "ghi", "def"));
		
		List<WordFillElementDTO> lwfWordFillElemList = new ArrayList<>();
		Iterator<List<String>> lwfTextIter = lwfText.iterator();
		Iterator<List<String>> lwfPossAnsIter = lwfPossibleAnswers.iterator();
		for (List<String> answerList: lwfAnswers)
		{
			List<String> textList = lwfTextIter.next();
			List<String> possibleAnswersList = lwfPossAnsIter.next();
			lwfWordFillElemList.add(new WordFillElementDTO(
					textList,
					answerList.stream()
						.map(ans -> new EmptySpaceDTO(ans))
						.collect(Collectors.toList()),
					true,
					possibleAnswersList));
		}
		
		return new ListWordFillDTO(
				"Test instruction", List.of(), 100,
				lwfWordFillElemList);
	}
	public static ListChoiceWordFill mockListChoiceWordFill()
	{
		List<List<String>> lcwfText = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		List<List<String>> lcwfAnswers = List.of(
				List.of("abc", "def"),
				List.of("ghi"),
				List.of("jkl"),
				List.of("mno"));
		
		List<ChoiceWordFillElement> lcwfWordFillElemList = new ArrayList<>();
		Iterator<List<String>> lcwfTextIter = lcwfText.iterator();
		for (List<String> answerList: lcwfAnswers)
		{
			List<String> textList = lcwfTextIter.next();
			lcwfWordFillElemList.add(new ChoiceWordFillElement(UUID.randomUUID(),
					textList,
					answerList.stream()
						.map(ans -> new WordChoice(UUID.randomUUID(), ans, List.of("qwe", "poi")))
						.collect(Collectors.toList()),
					true));
		}
		
		return new ListChoiceWordFill(UUID.randomUUID(),
				"Test instruction", List.of(),
				lcwfWordFillElemList, 100);
	}
	public static ListChoiceWordFillDTO mockListChoiceWordFillDTO()
	{
		List<List<String>> lcwfText = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		List<List<String>> lcwfAnswers = List.of(
				List.of("abc", "def"),
				List.of("ghi"),
				List.of("jkl"),
				List.of("mno"));
		
		List<ChoiceWordFillElementDTO> lcwfWordFillElemList = new ArrayList<>();
		Iterator<List<String>> lcwfTextIter = lcwfText.iterator();
		for (List<String> answerList: lcwfAnswers)
		{
			List<String> textList = lcwfTextIter.next();
			lcwfWordFillElemList.add(new ChoiceWordFillElementDTO(
					textList,
					answerList.stream()
						.map(ans -> new WordChoiceDTO(ans, List.of("qwe", "poi")))
						.collect(Collectors.toList()),
					true));
		}
		
		return new ListChoiceWordFillDTO(
				"Test instruction", List.of(), 100,
				lcwfWordFillElemList);
	}
	public static ChronologicalOrder mockChronologicalOrder()
	{
		List<String> coText = List.of("Lorem ipsum dolor sit amet",
				"consectetur adipiscing elit",
				"sed do eiusmod tempor incididunt",
				"ut labore et dolore magna aliqua",
				"Ut enim ad minim veniam",
				"quis nostrud exercitation",
				"ullamco laboris nisi ut",
				"aliquip ex ea commodo consequat");
		
		return new ChronologicalOrder(UUID.randomUUID(),
				"Test instruction", List.of(),
				coText, 100);
	}
	public static ChronologicalOrderDTO mockChronologicalOrderDTO()
	{
		List<String> coText = List.of("Lorem ipsum dolor sit amet",
				"consectetur adipiscing elit",
				"sed do eiusmod tempor incididunt",
				"ut labore et dolore magna aliqua",
				"Ut enim ad minim veniam",
				"quis nostrud exercitation",
				"ullamco laboris nisi ut",
				"aliquip ex ea commodo consequat");
		
		return new ChronologicalOrderDTO(
				"Test instruction", List.of(), 100,
				coText);
	}
	public static ListSentenceForming mockListSentenceForming()
	{
		List<List<String>> text = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		
		List<SentenceFormingElement> lsfWordFillElemList = text.stream()
				.map(textList -> new SentenceFormingElement(UUID.randomUUID(), textList))
				.collect(Collectors.toList());
		
		return new ListSentenceForming(UUID.randomUUID(),
				"Test instruction", List.of(),
				lsfWordFillElemList, 100);
	}
	public static ListSentenceFormingDTO mockListSentenceFormingDTO()
	{
		List<List<String>> text = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		
		List<SentenceFormingElementDTO> lsfWordFillElemList = text.stream()
				.map(textList -> new SentenceFormingElementDTO(textList))
				.collect(Collectors.toList());
		
		return new ListSentenceFormingDTO(
				"Test instruction", List.of(), 100,
				lsfWordFillElemList);
	}
	public static OptionSelect mockOptionSelect()
	{
		String osContent = "Lorem ipsum dolor sit amet";
		List<String> osCorrectAnswers = List.of(
				"eiusmod", "tempor", "incididunt ut");
		List<String> osIncorrectAnswers = List.of(
				"adipiscing", "elit", "sed", "labore", "et dolore");
		
		return new OptionSelect(UUID.randomUUID(), 
				"Test instruction", List.of(),
				new OptionSelectElement(UUID.randomUUID(),
						osContent, osCorrectAnswers, osIncorrectAnswers), 100);
	}
	public static OptionSelectDTO mockOptionSelectDTO()
	{
		String osContent = "Lorem ipsum dolor sit amet";
		List<String> osCorrectAnswers = List.of(
				"eiusmod", "tempor", "incididunt ut");
		List<String> osIncorrectAnswers = List.of(
				"adipiscing", "elit", "sed", "labore", "et dolore");
		
		return new OptionSelectDTO(
				"Test instruction", List.of(), 100,
				new OptionSelectElementDTO(
						osContent, osCorrectAnswers, osIncorrectAnswers));
	}
	public static WordConnect mockWordConnect()
	{
		List<String> wcLeftWords = List.of("Lorem", "ipsum", "dolor", "sit", "amet");
		List<String> wcRightWords = List.of("consectetur", "adipiscing", "elit", "sed do", "eiusmod");
		Map<Integer, Integer> wcCorrectMapping = Map.of(
				0, 3,
				1, 0,
				2, 4,
				3, 2,
				4, 1);
		
		return new WordConnect(UUID.randomUUID(),
				"Test instruction", List.of(),
				wcLeftWords, wcRightWords, wcCorrectMapping, 100);
	}
	public static WordConnectDTO mockWordConnectDTO()
	{
		List<String> wcLeftWords = List.of("Lorem", "ipsum", "dolor", "sit", "amet");
		List<String> wcRightWords = List.of("consectetur", "adipiscing", "elit", "sed do", "eiusmod");
		Map<Integer, Integer> wcCorrectMapping = Map.of(
				0, 3,
				1, 0,
				2, 4,
				3, 2,
				4, 1);
		
		return new WordConnectDTO(
				"Test instruction", List.of(), 100,
				wcLeftWords, wcRightWords, wcCorrectMapping);
	}
}
