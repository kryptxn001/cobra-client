package me.kryptxn.cobraclient;

import me.kryptxn.cobraclient.event.EventManager;
import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobraClientMod implements ClientModInitializer {
	public static final String MOD_ID = "cobraclient";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		System.out.println("Cobra Client starting...");

	}
}