package com.reseeit.com.reseeit.listeners;

import com.reseeit.models.Interaction;

import java.util.ArrayList;

public interface InteractionListener {
    Interaction getInteraction(String userId);

    void onInteractionSeen(Interaction interaction);

    ArrayList<Interaction> getInteractionListForCoupon(String userId);
}
