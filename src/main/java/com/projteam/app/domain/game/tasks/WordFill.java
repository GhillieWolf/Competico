package com.projteam.app.domain.game.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.projteam.app.domain.game.tasks.WordFillElement.EmptySpace;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.domain.game.tasks.answers.WordFillAnswer;
import com.projteam.app.dto.game.tasks.TaskInfoDTO;
import com.projteam.app.dto.game.tasks.WordFillElementDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class WordFill implements Task
{
	private @Id UUID id;
	private @ManyToOne WordFillElement content;
	
	private double difficulty;
	
	public List<String> getText()
	{
		return content.getText();
	}
	public List<EmptySpace> getEmptySpaces()
	{
		return content.getEmptySpaces();
	}
	public List<String> getPossibleAnswers()
	{
		return content.getPossibleAnswers();
	}
	
	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof WordFillAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		List<String> ansList = ((WordFillAnswer) answer).getAnswers();
		Iterator<String> iter = getEmptySpaces()
				.stream()
				.map(wc -> wc.getAnswer())
				.iterator();
		
		if (ansList == null)
			return 0;
		if (ansList.size() != getEmptySpaces().size())
			throw new IllegalArgumentException("Answer length differs from task size: "
					+ ansList.size() + ", " + getEmptySpaces().size());
		
		long score = 0;
		
		for (String s: ansList)
		{
			if (iter.next().equals(s))
				score++;
		}
		
		return ((double) score) / ansList.size();
	}
	@Override
	public Class<? extends TaskAnswer> getAnswerType()
	{
		return WordFillAnswer.class;
	}
	@Override
	public TaskInfoDTO toDTO(int taskNumber)
	{
		return new TaskInfoDTO("WordFill", taskNumber, 
				new WordFillElementDTO(content));
	}
}