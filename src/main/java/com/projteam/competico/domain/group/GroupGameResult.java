package com.projteam.competico.domain.group;

import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import com.projteam.competico.domain.game.GameResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class GroupGameResult
{
	private @Id UUID id;
	private @JoinColumn(name = "lecturer_group") @ManyToOne Group group;
	private @OneToOne GameResult gameResult;
}
