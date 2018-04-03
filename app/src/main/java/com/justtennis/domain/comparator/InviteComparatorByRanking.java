package com.justtennis.domain.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;

public class InviteComparatorByRanking implements Comparator<Invite> {

	private PlayerService playerService;
	private RankingService rankingService;
	private boolean estimate;
	private boolean inverse;
	@SuppressLint("UseSparseArrays")
	private Map<Long, Ranking> hashRanking = new HashMap<Long, Ranking>();

	public InviteComparatorByRanking(PlayerService playerService, RankingService rankingService, boolean estimate, boolean inverse) {
		this.playerService = playerService;
		this.rankingService = rankingService;
		this.estimate = estimate;
		this.inverse = inverse;
	}

	@Override
	public int compare(Invite lhs, Invite rhs) {
		Ranking lhsRaning = getRanking(lhs);
		Ranking rhsRaning = getRanking(rhs);
		int order = (lhsRaning.getOrder() > rhsRaning.getOrder() ? 1 : -1);
		if (inverse) {
			order = -order;
		}
		return order;
	}

	private Ranking getRanking(Invite invite) {
		Ranking ret = null;
		if (hashRanking.containsKey(invite.getId())) {
			ret = hashRanking.get(invite.getId());
		} else {
			Player player = playerService.find(invite.getPlayer().getId());
			ret = rankingService.getRanking(invite, player, estimate);
			hashRanking.put(invite.getId(), ret);
		}
		return ret;
	}
}
