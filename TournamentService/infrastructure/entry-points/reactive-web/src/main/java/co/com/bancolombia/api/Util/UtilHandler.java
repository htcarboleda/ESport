package co.com.bancolombia.api.Util;

import co.com.bancolombia.api.dto.request.TournamentCreateRequest;
import co.com.bancolombia.api.dto.response.TournamentDTO;
import co.com.bancolombia.model.category.Category;
import co.com.bancolombia.model.gametype.GameType;
import co.com.bancolombia.model.tournament.Tournament;
import co.com.bancolombia.model.tournamentadmin.TournamentAdmin;
import co.com.bancolombia.model.tournamentstage.TournamentStage;
import co.com.bancolombia.model.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static co.com.bancolombia.constant.Constants.NOT_FOUND;


@Component
@AllArgsConstructor
public class UtilHandler {

    public TournamentDTO tournamentToDto(Tournament rs) {

        return TournamentDTO.builder()
                .id(rs.getId())
                .name(rs.getName())
                .description(rs.getDescription())
                .category(Optional.ofNullable(rs.getCategory()).map(Category::getDescription).orElse(NOT_FOUND))
                .gameType(Optional.ofNullable(rs.getGameType()).map(GameType::getName).orElse(NOT_FOUND))
                .startDate(rs.getStartDate())
                .endDate(rs.getEndDate())
                .format(Optional.ofNullable(rs.getFormat()).map(Enum::toString).orElse(NOT_FOUND))
                .isFree(rs.getIsFree())
                .status(Optional.ofNullable(rs.getStatus()).map(Enum::toString).orElse(NOT_FOUND))
                .creator(Optional.ofNullable(rs.getCreator()).map(User::getFullName).orElse(NOT_FOUND))
                .createdAt(rs.getCreatedAt())
                .updatedAt(rs.getUpdatedAt())
                .build();
    }

    public Tournament createRqToTournament(TournamentCreateRequest rs) {
        return Tournament.builder()
                .name(rs.getName())
                .description(rs.getDescription())
                .categoryId(rs.getCategory())
                .gameTypeId(rs.getGameType())
                .startDate(rs.getStartDate())
                .endDate(rs.getEndDate())
                .format(rs.getFormat())
                .isFree(rs.getIsFree())
                .status(rs.getStatus())
                .maxTicketsParticipation(rs.getMaxTicketsParticipation())
                .maxTicketsSpectator(rs.getMaxTicketsSpectator())
                .creatorId(1)
                .admins(rs.getAdditionalAdmins() != null ?
                        rs.getAdditionalAdmins().stream()
                                .map(admin -> TournamentAdmin.builder()
                                        .userId(admin.getUserId())
                                        .role(admin.getRole())
                                        .build())
                                .toList() :
                        null
                )
                .stages(rs.getStages() != null ?
                        rs.getStages().stream()
                                .map(stage -> TournamentStage.builder()
                                        .name(stage.getName())
                                        .startDate(stage.getStartDate())
                                        .endDate(stage.getEndDate())
                                        .participantPrice(stage.getParticipantPrice())
                                        .spectatorPrice(stage.getSpectatorPrice())
                                        .maxParticipantTickets(stage.getMaxParticipantTickets())
                                        .maxSpectatorTickets(stage.getMaxSpectatorTickets())
                                        .freeParticipantSlots(stage.getFreeParticipantSlots())
                                        .paidParticipantSlots(stage.getPaidParticipantSlots())
                                        .freeSpectatorSlots(stage.getFreeSpectatorSlots())
                                        .paidSpectatorSlots(stage.getPaidSpectatorSlots())
                                        .build())
                                .toList() :
                        null
                )
                .build();
    }


}
