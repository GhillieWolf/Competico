package com.projteam.competico.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import com.fasterxml.jackson.databind.JsonNode;
import com.projteam.competico.dto.game.GameResultDTO;
import com.projteam.competico.dto.game.GameResultTotalDuringGameDTO;
import com.projteam.competico.dto.game.LeaderboardEntryDTO;
import com.projteam.competico.service.game.GameService;
import com.projteam.competico.service.game.PlayerDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "GameAPI", tags = "API managing games and their results")
public class GameAPI
{
	private GameService gameService;
	private PlayerDataService pdService;
	
	@Autowired
	public GameAPI(GameService gs, PlayerDataService pd)
	{
		gameService = gs;
		pdService = pd;
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Create a new game", code = 201)
	@ApiResponses(
	{
		@ApiResponse(code = 201, message = "Game created successfully"),
		@ApiResponse(code = 400, message = "Game could not be created")
	})
	@PostMapping("api/v1/lobby/{gameCode}/start")
	public boolean createGame(@PathVariable String gameCode)
	{
		return gameService.createGameFromLobby(gameCode);
	}
	
	@ApiOperation(value = "Check game status", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Current status of the game"),
	})
	@GetMapping("api/v1/game/{gameCode}")
	public Map<String, Object> gameStatus(@PathVariable String gameCode)
	{
		return Map.of("exists", gameService.gameExists(gameCode));
	}
	
	@ApiOperation(value = "Get current task", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Current task"),
	})
	@GetMapping("api/v1/game/{gameCode}/tasks/current")
	public Object getCurrentTask(@PathVariable String gameCode)
	{
		if (gameService.hasGameFinished(gameCode))
			return Map.of("hasGameFinished", true);
		return gameService.getCurrentTaskInfo(gameCode);
	}
	
	@ApiOperation(value = "Send answers to the current task", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Answers received and graded"),
		@ApiResponse(code = 400, message = "Invalid answer content")
	})
	@PostMapping("api/v1/game/{gameCode}/tasks/answer")
	public ResponseEntity<Object> answer(@PathVariable String gameCode, @RequestBody JsonNode answer)
	{
		try
		{
			gameService.acceptAnswer(gameCode, answer);
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Niepoprawny format odpowiedzi na zadanie");
		}
	}
	
	@ApiOperation(value = "Get total results of this game", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Total results of this game")
	})
	@GetMapping("api/v1/scores/{gameID}/total")
	public List<? extends GameResultDTO> getTotalResults(@PathVariable UUID gameID)
	{
		Optional<List<GameResultTotalDuringGameDTO>> ret =
				gameService.getCurrentResults(gameID);
		if (ret.isEmpty())
			return gameService.getResults(gameID).orElse(null);
		return ret.get();
	}
	@ApiOperation(value = "Get personal results of this game", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Personal results of this game")
	})
	@GetMapping("api/v1/scores/{gameID}/personal")
	public List<? extends GameResultDTO> getPersonalResults(@PathVariable UUID gameID)
	{
		return gameService.getPersonalResults(gameID).orElse(null);
	}
	@ApiOperation(value = "Get personal results of chosen user for this game", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Personal results of this game")
	})
	@GetMapping("api/v1/scores/{gameID}/personal/{username}")
	public List<? extends GameResultDTO> getPersonalResults(
			@PathVariable UUID gameID,
			@PathVariable String username)
	{
		return gameService.getPersonalResults(gameID, username).orElse(null);
	}
	
	@ApiOperation(value = "Check if total results for this game changed", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Whether total results for this game changed")
	})
	@GetMapping("api/v1/scores/{gameID}/total/changes")
	public Object haveResultsChanged(@PathVariable UUID gameID)
	{
		try
		{
			return gameService.haveResultsChanged(gameID)
					.map(resultsChanged -> Map.of(
							"haveResultsChanged", resultsChanged))
					.orElse(Map.of("gameExists", false));
		}
		catch (IllegalArgumentException e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@ApiOperation(value = "Notify the server that the user is still in a game", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Server notified successfully")
	})
	@PostMapping("api/v1/game/{gameCode}/ping")
	public void noteInteraction(@PathVariable String gameCode)
	{
		gameService.noteInteraction(gameCode);
	}
	
	@ApiOperation(value = "Retrieve a list of finished games", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Success")
	})
	@GetMapping("api/v1/game/history/{page}")
	public Object getGameHistory(@PathVariable int page)
	{
		if (page < 1)
			return new RedirectView("api/v1/game/history/1");
		return gameService.getHistory(page - 1);
	}
	
	@ApiOperation(value = "Check the rating of the current user", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Rating retrieved successfully")
	})
	@GetMapping("api/v1/player/rating")
	public Object getRating()
	{
		try
		{
			return gameService.getRating()
					.map(rating -> Map.<String, Object>of("rating", rating))
					.orElse(Map.of("hasRating", false));
		}
		catch (IllegalArgumentException e)
		{
			return Map.of("exists", false);
		}
	}
	@ApiOperation(value = "Check the rating of a user with the provided username", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Rating retrieved successfully")
	})
	@GetMapping("api/v1/player/{username}/rating")
	public Object getRating(@PathVariable String username)
	{
		try
		{
			return gameService.getRatingByUsername(username)
					.map(rating -> Map.<String, Object>of("rating", rating))
					.orElse(Map.of("hasRating", false));
		}
		catch (IllegalArgumentException e)
		{
			return Map.of("exists", false);
		}
	}
	
	@ApiOperation(value = "Get top players from leaderboard", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Leaderboard received")
	})
	@GetMapping("api/v1/game/leaderboard/top")
	public List<LeaderboardEntryDTO> getTopLeaderboard()
	{
		return pdService.getTopLeaderboard();
	}
	@ApiOperation(value = "Get players around the current player's rating from leaderboard", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Leaderboard received")
	})
	@GetMapping("api/v1/game/leaderboard/relative")
	public List<LeaderboardEntryDTO> getRelativeLeaderboard()
	{
		return pdService.getRelativeLeaderboard();
	}
}
