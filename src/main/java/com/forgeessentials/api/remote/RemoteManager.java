package com.forgeessentials.api.remote;

import com.google.gson.Gson;

public interface RemoteManager {

	public static class DefaultRemoteHandlerManager implements RemoteManager {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.forgeessentials.api.remote.RemoteManager#convertJsonObject(com.
		 * google.gson.JsonObject, java.lang.Class)
		 */
		@Override
		public Gson getGson() {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.forgeessentials.api.remote.RemoteManager#getHandler(java.lang.
		 * String)
		 */
		@Override
		public RemoteHandler getHandler(String id) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.forgeessentials.api.remote.RemoteManager#registerHandler(com.
		 * forgeessentials.api.remote.RemoteHandler)
		 */
		@Override
		public void registerHandler(RemoteHandler handler, String id) {
			/* do nothing */
		}

	}

	Gson getGson();

	RemoteHandler getHandler(String id);

	void registerHandler(RemoteHandler handler, String id);

}
