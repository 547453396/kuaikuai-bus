package cn.kuaikuai.trip.view.imageviewer;

import android.os.Build.VERSION;
import android.view.View;

public class Compat {
	
	private static final int SIXTY_FPS_INTERVAL = 1000 / 60;
	
	public static void postOnAnimation(View view, Runnable runnable) {
		if (VERSION.SDK_INT >= 17) {
			SDK16.postOnAnimation(view, runnable);
		} else {
			view.postDelayed(runnable, SIXTY_FPS_INTERVAL);
		}
	}

}
