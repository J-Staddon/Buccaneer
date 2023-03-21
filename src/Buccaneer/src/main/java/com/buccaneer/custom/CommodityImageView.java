package com.buccaneer.custom;

import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.backend.cards.chance.ChanceCard21;
import com.buccaneer.backend.cards.chance.ChanceCard25;
import com.buccaneer.backend.cards.chance.ChanceCard26;
import com.buccaneer.models.commodities.CrewCard;
import com.buccaneer.models.commodities.ICommodity;
import com.buccaneer.models.commodities.Treasure;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CommodityImageView extends ImageView {

    private final ICommodity commodity;

    /**
     * Handles the images
     * @param commodity
     * @param secure
     */
    public CommodityImageView(ICommodity commodity, boolean secure) {
        super();
        this.commodity = commodity;

        StringBuilder url = new StringBuilder();
        if (commodity instanceof ChanceCardBase) {
            if (commodity instanceof ChanceCard21)
                url.append("Chance_Crew");
            else if (commodity instanceof ChanceCard25 || commodity instanceof ChanceCard26)
                url.append("Chance_Anchor");
            else
                url.append("Chance");
            Tooltip.install(this, new Tooltip(((ChanceCardBase) commodity).getName()));
        } else if (commodity instanceof Treasure) {
            if (secure)
                url.append("Lock_");
            url.append(((Treasure) commodity).getName());
        } else
            url.append(((CrewCard) commodity).isRed() ? "red" : "black").append(commodity.getValue());
        url.append(".png");

        super.setImage(new Image(getClass().getResourceAsStream(url.toString())));
        super.setId(url.toString());
    }

    /**
     * @return
     */
    public ICommodity getCommodity() {
        return commodity;
    }
}
