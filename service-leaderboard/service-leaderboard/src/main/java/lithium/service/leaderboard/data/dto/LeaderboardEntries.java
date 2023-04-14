package lithium.service.leaderboard.data.dto;

import lithium.service.leaderboard.data.entities.Domain;
import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.LeaderboardPlaceNotification;
import lithium.service.leaderboard.data.projections.LeaderboardProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntries {
	private Leaderboard leaderboard;
	private List<LeaderboardEntryBasic> entries;
	private List<LeaderboardPlaceNotification> leaderboardPlaceNotifications;
	private Domain domain;
}