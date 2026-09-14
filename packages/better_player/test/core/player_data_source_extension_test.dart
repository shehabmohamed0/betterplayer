import 'dart:io';

import 'package:better_player/better_player.dart';
import 'package:flutter_test/flutter_test.dart';

import '../helpers/better_player_test_utils.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(BetterPlayerTestUtils.setupMockPlatform);

  for (final videoFormat in [VideoFormat.hls, VideoFormat.dash]) {
    test(
      'setupDataSource skips ${videoFormat.name} manifest fetch when ASMS flags are false',
      () async {
        var dartRequests = 0;
        final controller = BetterPlayerController(const PlayerConfiguration());

        await HttpOverrides.runZoned(
          () => controller.setupDataSource(
            PlayerDataSource.network(
              'https://example.com/stream',
              videoFormat: videoFormat,
              useAsmsTracks: false,
              useAsmsAudioTracks: false,
              useAsmsSubtitles: false,
            ),
          ),
          createHttpClient: (_) {
            dartRequests++;
            throw StateError('ASMS manifest should not be fetched');
          },
        );

        expect(dartRequests, 0);
        controller.dispose(forceDispose: true);
      },
    );
  }

  test(
    'setupDataSource still fetches ASMS manifests when parsing is enabled',
    () async {
      var dartRequests = 0;
      final controller = BetterPlayerController(const PlayerConfiguration());

      await HttpOverrides.runZoned(
        () => controller.setupDataSource(
          PlayerDataSource.network(
            'https://example.com/stream',
            videoFormat: VideoFormat.hls,
          ),
        ),
        createHttpClient: (_) {
          dartRequests++;
          throw StateError(
            'Stop after proving the manifest fetch was attempted',
          );
        },
      );

      expect(dartRequests, 1);
      controller.dispose(forceDispose: true);
    },
  );
}
