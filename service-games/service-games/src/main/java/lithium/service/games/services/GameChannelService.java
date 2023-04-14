package lithium.service.games.services;

import lithium.service.games.data.entities.Channel;
import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameChannel;
import lithium.service.games.data.repositories.ChannelRepository;
import lithium.service.games.data.repositories.GameChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lithium.service.games.enums.GameChannelsEnum.ANDROID_NATIVE;
import static lithium.service.games.enums.GameChannelsEnum.DESKTOP_WEB;
import static lithium.service.games.enums.GameChannelsEnum.MOBILE_IOS;
import static lithium.service.games.enums.GameChannelsEnum.MOBILE_WEB;

@Service
public class GameChannelService {

    @Autowired ChannelRepository channelRepository;
    @Autowired GameChannelRepository gameChannelRepository;

    public void createGameChannels() {
        create(DESKTOP_WEB.gameChannelName);
        create(MOBILE_WEB.gameChannelName);
        create(MOBILE_IOS.gameChannelName);
        create(ANDROID_NATIVE.gameChannelName);
    }

    private Channel create(String channelName) {
        return channelRepository.findOrCreateByName(channelName, () -> Channel.builder().name(channelName).build());
    }

    private Channel find(String channelName) {
        return channelRepository.findByName(channelName);
    }

    private void addChannelToGame(Game game, String channel) {
       gameChannelRepository.save(GameChannel.builder().game(game).channel(find(channel)).build());
    }

    private void removeGameChannel(GameChannel gameChannel) {
        gameChannelRepository.delete(gameChannel);
    }

    /**
     * Toggles a games channels from a new full channel list
     * @param channels after change only channels provided should be active, the rest will be deactivated
     * @param game the game to which the channels should take effect on
     */
    public void toggleGameChannels(List<String> channels, Game game) {
        List<GameChannel> activeGameChannels = game.getGameChannels();
        if (channels != null || !channels.isEmpty()) {
            // Checks if any new channels needs to be added
            for (String channel : channels) {
                Optional<GameChannel> gameChannel = activeGameChannels.stream()
                        .filter(activeGameChannel -> activeGameChannel.getChannel().getName().equals(channel))
                        .findAny();
                if (!gameChannel.isPresent()) {
                    addChannelToGame(game, channel);
                    continue;
                }
                // Determines the delta betwen old active channels and new channels; then removes delta
                activeGameChannels.remove(gameChannel.get());
            }
        }
        // Removes delta channels no longer active
        for (GameChannel gameChannel : activeGameChannels) {
            removeGameChannel(gameChannel);
        }
    }

    public List<String> getChannels(Game dbGame) {
        List<GameChannel> gameChannelByGame = gameChannelRepository.findGameChannelByGame(dbGame);
        List<String> channels = new ArrayList<>();
        if (gameChannelByGame == null || gameChannelByGame.isEmpty()) { return channels; }
        for (GameChannel gameChannel: gameChannelByGame){
            channels.add((gameChannel.getChannel().getName()));
        }
        return channels;
    }

    public  List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }
}
