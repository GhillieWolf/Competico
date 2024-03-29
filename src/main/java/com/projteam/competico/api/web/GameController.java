package com.projteam.competico.api.web;

import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.swagger.annotations.ApiOperation;

@Controller
public class GameController
{
	@GetMapping("/game/results/{gameID}")
	@ApiOperation(value = "Display the results of a given game.")
	public String lobbyPage(@PathVariable("gameID") UUID gameID)
	{
		return "game-results";
	}
	@GetMapping("/game/history/{page}")
	@ApiOperation(value = "Display a list of the game history of the current user.")
	public String gameHistory()
	{
		return "game-history";
	}
}
