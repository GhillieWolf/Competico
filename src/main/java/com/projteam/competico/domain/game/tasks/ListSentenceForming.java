package com.projteam.competico.domain.game.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import com.projteam.competico.domain.game.tasks.answers.ListSentenceFormingAnswer;
import com.projteam.competico.domain.game.tasks.answers.TaskAnswer;
import com.projteam.competico.dto.game.tasks.show.ListSentenceFormingDTO;
import com.projteam.competico.dto.game.tasks.show.TaskInfoDTO;
import com.projteam.competico.utils.Initializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class ListSentenceForming implements Task
{
	private @Id UUID id;
	private @Column(length = 1024) String instruction;
	private @ElementCollection List<String> tags;
	private @ManyToMany @OrderColumn List<SentenceFormingElement> rows;

	private double difficulty;
	
	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof ListSentenceFormingAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		List<List<String>> answers = ((ListSentenceFormingAnswer) answer).getAnswers();
		if (answers == null)
			return 0;
		
		Iterator<List<String>> iter = rows.stream()
				.map(row -> row.getWords())
				.iterator();
		
		long l = rows.stream()
				.mapToLong(row -> row.getWords().size())
				.sum();
		if (l == 0)
			return 1;
		
		long score = 0;
		
		for (List<String> row: answers)
		{
			if (row == null)
				continue;
			List<String> currList = iter.next();
			if (row.size() != currList.size())
				continue;
			Iterator<String> currIt = currList.iterator();
			for (String ans: row)
			{
				if (currIt.next().equals(ans))
					score++;
			}
		}
		
		return ((double) score) / l;
	}
	@Override
	public Class<? extends TaskAnswer> getAnswerType()
	{
		return ListSentenceFormingAnswer.class;
	}
	@Override
	public TaskInfoDTO prepareTaskInfo(int currentTaskNumber, int taskCount)
	{
		return new TaskInfoDTO("ListSentenceForming", currentTaskNumber, taskCount, instruction,
				new ListSentenceFormingDTO(this));
	}
	@Override
	public void initialize()
	{
		Initializable.initialize(tags, rows);
	}
}
