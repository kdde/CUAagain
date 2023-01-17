package com.green.service;

import org.springframework.ui.Model;

import com.green.domain.dto.TeamDTO;

public interface TeamService {

	void getTeamList(Model model);

	void save(TeamDTO dto, long dep_id);

	void getTeamListOfDep(long depId, Model model);



}