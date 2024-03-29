package com.projteam.competico.integration.game;

import static com.projteam.competico.domain.Account.PLAYER_ROLE;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.competico.config.SecurityContextConfig;
import com.projteam.competico.dao.AccountDAO;
import com.projteam.competico.dao.game.PlayerDataDAO;
import com.projteam.competico.domain.Account;
import com.projteam.competico.domain.game.PlayerData;
import com.projteam.competico.dto.game.tasks.show.ChoiceWordFillElementDTO;
import com.projteam.competico.dto.game.tasks.show.ChronologicalOrderDTO;
import com.projteam.competico.dto.game.tasks.show.ListChoiceWordFillDTO;
import com.projteam.competico.dto.game.tasks.show.ListSentenceFormingDTO;
import com.projteam.competico.dto.game.tasks.show.ListWordFillDTO;
import com.projteam.competico.dto.game.tasks.show.OptionSelectDTO;
import com.projteam.competico.dto.game.tasks.show.WordConnectDTO;
import com.projteam.competico.dto.game.tasks.show.WordFillElementDTO;
import com.projteam.competico.service.game.GameService;
import com.projteam.competico.service.game.PlayerDataService;

@SpringBootTest
@ContextConfiguration(name = "Integration-tests")
@AutoConfigureMockMvc(addFilters = false)
public class GameTests
{
	@Autowired
	private MockMvc mvc;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	private @MockBean AccountDAO accDao;
	private @MockBean PlayerDataDAO pdDao;
	
	private @MockBean SecurityContextConfig secConf;
	
	@Autowired
	private GameService gameServ;
	
	private static final MediaType APPLICATION_JSON_UTF8 =
			new MediaType(MediaType.APPLICATION_JSON.getType(),
					MediaType.APPLICATION_JSON.getSubtype(),
					Charset.forName("utf8"));
	
	@RepeatedTest(value = 15)
	public void canCompleteFullGameWhileGettingResults() throws Exception
	{
		Account host = new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail("testHost@test.pl")
				.withUsername("TestHostAccount")
				.withNickname("TestHostAccount")
				.withPassword("QWERTY")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		Account player = new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail("testPlayer@test.pl")
				.withUsername("TestPlayerAccount")
				.withNickname("TestPlayerAccount")
				.withPassword("QWERTY")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		PlayerData pdHost = new PlayerData(UUID.randomUUID(),
				host, PlayerDataService.DEFAULT_RATING);
		PlayerData pdPlayer = new PlayerData(UUID.randomUUID(),
				host, PlayerDataService.DEFAULT_RATING);
		int playerCount = 2;
		
		when(accDao.findByEmailOrUsername(host.getEmail(), host.getEmail()))
			.thenReturn(Optional.of(host));
		when(accDao.findByEmailOrUsername(host.getEmail(), host.getUsername()))
			.thenReturn(Optional.of(host));
		when(accDao.findByEmailOrUsername(host.getUsername(), host.getEmail()))
			.thenReturn(Optional.of(host));
		when(accDao.findByEmailOrUsername(host.getUsername(), host.getUsername()))
			.thenReturn(Optional.of(host));
		
		when(accDao.findByEmailOrUsername(player.getEmail(), player.getEmail()))
			.thenReturn(Optional.of(player));
		when(accDao.findByEmailOrUsername(player.getEmail(), player.getUsername()))
			.thenReturn(Optional.of(player));
		when(accDao.findByEmailOrUsername(player.getUsername(), player.getEmail()))
			.thenReturn(Optional.of(player));
		when(accDao.findByEmailOrUsername(player.getUsername(), player.getUsername()))
			.thenReturn(Optional.of(player));
		
		when(pdDao.findByAccount_id(host.getId())).thenReturn(Optional.of(pdHost));
		when(pdDao.findByAccount_id(player.getId())).thenReturn(Optional.of(pdPlayer));
		
		SecurityContext sec = mock(SecurityContext.class);
		when(secConf.getContext()).thenReturn(sec);
		
		Authentication hostAuth = mock(Authentication.class);
		Authentication playerAuth = mock(Authentication.class);
		when(hostAuth.getPrincipal()).thenReturn(host);
		when(hostAuth.isAuthenticated()).thenReturn(true);
		when(playerAuth.getPrincipal()).thenReturn(player);
		when(playerAuth.isAuthenticated()).thenReturn(true);
		
		when(accDao.findById(host.getId())).thenReturn(Optional.of(host));
		when(accDao.findById(player.getId())).thenReturn(Optional.of(player));
		
		switchAccount(hostAuth, sec);
		String gameCode = mvc.perform(post("/api/v1/lobby"))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();
		assertNotNull(gameCode);
		
		switchAccount(playerAuth, sec);
		mvc.perform(post("/api/v1/lobby/join/" + gameCode))
				.andExpect(status().isOk());
		
		switchAccount(hostAuth, sec);
		mvc.perform(post("/api/v1/lobby/" + gameCode + "/start"))
				.andExpect(status().isCreated());
		
		String gameID = mapper.readTree(mvc.perform(get("/api/v1/playerinfo"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString())
				.get("gameID")
				.asText();
		
		int taskCount = gameServ.getTaskCount(gameCode);
		for (int i = 0; i < taskCount; i++)
		{
			switchAccount(hostAuth, sec);
			JsonNode ti1 = mapper.readTree(
				mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.currentTaskNumber", is(i)))
					.andExpect(jsonPath("$.taskCount", is(taskCount)))
					.andReturn()
					.getResponse()
					.getContentAsString());
			mvc.perform(post("/api/v1/game/" + gameCode + "/tasks/answer")
					.contentType(APPLICATION_JSON_UTF8)
					.content(sampleAnswer(ti1)))
				.andExpect(status().isOk());
			
			switchAccount(playerAuth, sec);
			JsonNode ti2 = mapper.readTree(
				mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.currentTaskNumber", is(i)))
					.andExpect(jsonPath("$.taskCount", is(taskCount)))
					.andReturn()
					.getResponse()
					.getContentAsString());
				mvc.perform(post("/api/v1/game/" + gameCode + "/tasks/answer")
						.contentType(APPLICATION_JSON_UTF8)
						.content(sampleAnswer(ti2)))
					.andExpect(status().isOk());
				
			assertCanGetResults(gameID, playerCount, i + 1, hostAuth, playerAuth, sec);
		}
		
		assertFalse(gameServ.gameExists(gameCode));
		
		switchAccount(hostAuth, sec);
		mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.hasGameFinished", is(true)));
		switchAccount(playerAuth, sec);
		mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.hasGameFinished", is(true)));
		
		assertCanGetResults(gameID, playerCount, taskCount, hostAuth, playerAuth, sec);
	}
	@RepeatedTest(value = 15)
	public void canCompleteFullGameWithoutGettingResultsMidGame() throws Exception
	{
		Account host = new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail("testHost@test.pl")
				.withUsername("TestHostAccount")
				.withNickname("TestHostAccount")
				.withPassword("QWERTY")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		Account player = new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail("testPlayer@test.pl")
				.withUsername("TestPlayerAccount")
				.withNickname("TestPlayerAccount")
				.withPassword("QWERTY")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		PlayerData pdHost = new PlayerData(UUID.randomUUID(),
				host, PlayerDataService.DEFAULT_RATING);
		PlayerData pdPlayer = new PlayerData(UUID.randomUUID(),
				host, PlayerDataService.DEFAULT_RATING);
		int playerCount = 2;
		
		when(accDao.findByEmailOrUsername(host.getEmail(), host.getEmail()))
			.thenReturn(Optional.of(host));
		when(accDao.findByEmailOrUsername(host.getEmail(), host.getUsername()))
			.thenReturn(Optional.of(host));
		when(accDao.findByEmailOrUsername(host.getUsername(), host.getEmail()))
			.thenReturn(Optional.of(host));
		when(accDao.findByEmailOrUsername(host.getUsername(), host.getUsername()))
			.thenReturn(Optional.of(host));
		
		when(accDao.findByEmailOrUsername(player.getEmail(), player.getEmail()))
			.thenReturn(Optional.of(player));
		when(accDao.findByEmailOrUsername(player.getEmail(), player.getUsername()))
			.thenReturn(Optional.of(player));
		when(accDao.findByEmailOrUsername(player.getUsername(), player.getEmail()))
			.thenReturn(Optional.of(player));
		when(accDao.findByEmailOrUsername(player.getUsername(), player.getUsername()))
			.thenReturn(Optional.of(player));

		when(pdDao.findByAccount_id(host.getId())).thenReturn(Optional.of(pdHost));
		when(pdDao.findByAccount_id(player.getId())).thenReturn(Optional.of(pdPlayer));
		
		SecurityContext sec = mock(SecurityContext.class);
		when(secConf.getContext()).thenReturn(sec);
		
		Authentication hostAuth = mock(Authentication.class);
		Authentication playerAuth = mock(Authentication.class);
		when(hostAuth.getPrincipal()).thenReturn(host);
		when(hostAuth.isAuthenticated()).thenReturn(true);
		when(playerAuth.getPrincipal()).thenReturn(player);
		when(playerAuth.isAuthenticated()).thenReturn(true);
		
		when(accDao.findById(host.getId())).thenReturn(Optional.of(host));
		when(accDao.findById(player.getId())).thenReturn(Optional.of(player));
		
		switchAccount(hostAuth, sec);
		String gameCode = mvc.perform(post("/api/v1/lobby"))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();
		assertNotNull(gameCode);
		
		switchAccount(playerAuth, sec);
		mvc.perform(post("/api/v1/lobby/join/" + gameCode))
				.andExpect(status().isOk());
		
		switchAccount(hostAuth, sec);
		mvc.perform(post("/api/v1/lobby/" + gameCode + "/start"))
				.andExpect(status().isCreated());
		
		String gameID = mapper.readTree(mvc.perform(get("/api/v1/playerinfo"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString())
				.get("gameID")
				.asText();
		
		int taskCount = gameServ.getTaskCount(gameCode);
		for (int i = 0; i < taskCount; i++)
		{
			switchAccount(hostAuth, sec);
			JsonNode ti1 = mapper.readTree(
				mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.currentTaskNumber", is(i)))
					.andExpect(jsonPath("$.taskCount", is(taskCount)))
					.andReturn()
					.getResponse()
					.getContentAsString());
			mvc.perform(post("/api/v1/game/" + gameCode + "/tasks/answer")
					.contentType(APPLICATION_JSON_UTF8)
					.content(sampleAnswer(ti1)))
				.andExpect(status().isOk());
			
			switchAccount(playerAuth, sec);
			JsonNode ti2 = mapper.readTree(
				mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.currentTaskNumber", is(i)))
					.andExpect(jsonPath("$.taskCount", is(taskCount)))
					.andReturn()
					.getResponse()
					.getContentAsString());
			mvc.perform(post("/api/v1/game/" + gameCode + "/tasks/answer")
					.contentType(APPLICATION_JSON_UTF8)
					.content(sampleAnswer(ti2)))
				.andExpect(status().isOk());
		}
		
		assertFalse(gameServ.gameExists(gameCode));
		
		switchAccount(hostAuth, sec);
		mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.hasGameFinished", is(true)));
		switchAccount(playerAuth, sec);
		mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.hasGameFinished", is(true)));
		
		assertCanGetResults(gameID, playerCount, taskCount, hostAuth, playerAuth, sec);
	}
	
	private void assertCanGetResults(String gameID,
			int playerCount,
			int taskCount,
			Authentication hostAuth,
			Authentication playerAuth,
			SecurityContext sec) throws Exception
	{
		switchAccount(hostAuth, sec);
		mvc.perform(get("/api/v1/scores/" + gameID + "/total"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(playerCount)));
		mvc.perform(get("/api/v1/scores/" + gameID + "/personal"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(taskCount)));
		mvc.perform(get("/api/v1/scores/" + gameID + "/total/changes"))
			.andExpect(status().isOk());
		
		switchAccount(playerAuth, sec);
		mvc.perform(get("/api/v1/scores/" + gameID + "/total"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(playerCount)));
		mvc.perform(get("/api/v1/scores/" + gameID + "/personal"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(taskCount)));
		mvc.perform(get("/api/v1/scores/" + gameID + "/total/changes"))
			.andExpect(status().isOk());
	}

	//---Helpers---
	
	private void switchAccount(Authentication auth, SecurityContext sec)
	{
		when(sec.getAuthentication()).thenReturn(auth);
	}
	private String sampleAnswer(JsonNode taskInfo) throws JsonMappingException, JsonProcessingException
	{
		switch (taskInfo.get("taskName").asText())
		{
			case "WordFill":
				WordFillElementDTO wfeDto = mapper.treeToValue(taskInfo.get("task"),
						WordFillElementDTO.class);
				return mapper.valueToTree(
						Map.of("answers",
							wfeDto.getPossibleAnswers()
							.stream()
							.limit(wfeDto.getEmptySpaceCount())
							.collect(Collectors.toList())))
						.toString();
			case "ListWordFill":
				ListWordFillDTO lwf = mapper.treeToValue(taskInfo.get("task"),
						ListWordFillDTO.class);
				List<List<String>> possibleAnswers = lwf.getPossibleAnswers();
				List<Integer> emptySpaceCounts = lwf.getEmptySpaceCount();
				List<List<String>> lwfRet = new ArrayList<>();
				int l = possibleAnswers.size();
				for (int i = 0; i < l; i++)
				{
					List<String> list = possibleAnswers.get(i);
					lwfRet.add(list.stream()
							.limit(emptySpaceCounts.get(i))
							.collect(Collectors.toList()));
				}
				return mapper.valueToTree(Map.of("answers", lwfRet)).toString();
			case "ChoiceWordFill":
				ChoiceWordFillElementDTO cwfeDto = mapper.treeToValue(taskInfo.get("task"),
						ChoiceWordFillElementDTO.class);
				return mapper.valueToTree(
						Map.of("answers",
							cwfeDto.getWordChoices()
							.stream()
							.map(list -> list.stream()
									.findFirst()
									.orElse(null))
							.collect(Collectors.toList())))
						.toString();
			case "ListChoiceWordFill":
				ListChoiceWordFillDTO lcwf = mapper.treeToValue(taskInfo.get("task"),
						ListChoiceWordFillDTO.class);
				return mapper.valueToTree(Map.of("answers", lcwf.getWordChoices()
						.stream()
						.map(list -> list.stream()
								.map(li -> li.stream()
										.findFirst()
										.orElse(null))
								.collect(Collectors.toList()))
						.collect(Collectors.toList()))).toString();
			case "OptionSelect":
				OptionSelectDTO os = mapper.treeToValue(taskInfo.get("task"),
						OptionSelectDTO.class);
				return mapper.valueToTree(Map.of("answers", os.getAnswers())).toString();
			case "WordConnect":
				WordConnectDTO wcDto = mapper.treeToValue(taskInfo.get("task"),
						WordConnectDTO.class);
				int leftSize = wcDto.getLeftWords().size();
				int rightSize = wcDto.getLeftWords().size();
				List<Integer> rightList = IntStream.range(0, rightSize)
						.mapToObj(i -> i)
						.collect(Collectors.toList());
				return mapper.valueToTree(
						Map.of("answerMapping", IntStream.range(0, leftSize)
								.mapToObj(i -> i)
								.collect(Collectors.toMap(i -> i, i -> rightList.get(i)))))
							.toString();
			case "ChronologicalOrder":
				ChronologicalOrderDTO coDto = mapper.treeToValue(taskInfo.get("task"),
						ChronologicalOrderDTO.class);
				return mapper.valueToTree(
						Map.of("answers", coDto.getSentences()))
							.toString();
			case "ListSentenceForming":
				ListSentenceFormingDTO lsfDTO = mapper.treeToValue(taskInfo.get("task"),
						ListSentenceFormingDTO.class);
				return mapper.valueToTree(
						Map.of("answers", lsfDTO.getWords())
						).toString();
			default:
				return "[]";
		}
	}
}
