package co.com.bancolombia.r2dbc.entities;

import co.com.bancolombia.model.tournamentstage.TournamentStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("tournament_stages")
public class TournamentStageEntity {

    @Id
    @Column("id_tournament_stage")
    private Integer id;

    @Column("fk_id_tournament")
    private Integer tournamentId;

    @Column("name")
    private String name;

    @Column("start_date")
    private LocalDate startDate;

    @Column("end_date")
    private LocalDate endDate;

    @Column("participant_price")
    private BigDecimal participantPrice;

    @Column("spectator_price")
    private BigDecimal spectatorPrice;

    @Column("max_participant_tickets")
    private Integer maxParticipantTickets;

    @Column("max_spectator_tickets")
    private Integer maxSpectatorTickets;

    @Column("free_participant_slots")
    private Integer freeParticipantSlots;

    @Column("paid_participant_slots")
    private Integer paidParticipantSlots;

    @Column("free_spectator_slots")
    private Integer freeSpectatorSlots;

    @Column("paid_spectator_slots")
    private Integer paidSpectatorSlots;

    @Column("created_at")
    private Timestamp createdAt;

    @Column("updated_at")
    private Timestamp updatedAt;



    public static TournamentStageEntity fromDomain(TournamentStage tournamentStage) {

        return TournamentStageEntity.builder()
                .id(tournamentStage.getId())
                .tournamentId(tournamentStage.getTournamentId())
                .name(tournamentStage.getName())
                .startDate(tournamentStage.getStartDate())
                .endDate(tournamentStage.getEndDate())
                .participantPrice(tournamentStage.getParticipantPrice())
                .spectatorPrice(tournamentStage.getSpectatorPrice())
                .maxParticipantTickets(tournamentStage.getMaxParticipantTickets())
                .maxSpectatorTickets(tournamentStage.getMaxSpectatorTickets())
                .freeParticipantSlots(tournamentStage.getFreeParticipantSlots())
                .paidParticipantSlots(tournamentStage.getPaidParticipantSlots())
                .freeSpectatorSlots(tournamentStage.getFreeSpectatorSlots())
                .paidSpectatorSlots(tournamentStage.getPaidSpectatorSlots())
                .createdAt(tournamentStage.getCreatedAt())
                .updatedAt(tournamentStage.getUpdatedAt())
                .build();
    }

    public TournamentStage toDomain() {

        return TournamentStage.builder()
                .id(this.getId())
                .tournamentId(this.getTournamentId())
                .name(this.getName())
                .startDate(this.getStartDate())
                .endDate(this.getEndDate())
                .participantPrice(this.getParticipantPrice())
                .spectatorPrice(this.getSpectatorPrice())
                .maxParticipantTickets(this.getMaxParticipantTickets())
                .maxSpectatorTickets(this.getMaxSpectatorTickets())
                .freeParticipantSlots(this.getFreeParticipantSlots())
                .paidParticipantSlots(this.getPaidParticipantSlots())
                .freeSpectatorSlots(this.getFreeSpectatorSlots())
                .paidSpectatorSlots(this.getPaidSpectatorSlots())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }
}
