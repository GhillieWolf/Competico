package com.projteam.competico.domain.group;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.projteam.competico.domain.Account;
import com.projteam.competico.utils.Initializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LecturerGroup")
@Access(AccessType.FIELD)
public class Group implements Initializable
{
	private @Id UUID id;
	private @Column(name = "name") String name;
	private @Column(name = "creationDate") @Temporal(TemporalType.TIMESTAMP) Date creationDate;
	private @Column(name = "groupCode", unique = true) String groupCode;
	private @ElementCollection List<String> gameCodes;
	private @ManyToMany List<Account> lecturers;
	private @ManyToMany List<Account> players;
	
	public Group(UUID id, String groupName, String groupCode, List<Account> lecturers)
	{
		this.id = id;
		this.name = groupName;
		this.groupCode = groupCode;
		this.lecturers = new ArrayList<>(lecturers);
		creationDate = new Date();
		gameCodes = new ArrayList<>();
		players = new ArrayList<>();
	}

	@Override
	public void initialize()
	{
		Initializable.initialize(gameCodes, lecturers, players);
	}
}
