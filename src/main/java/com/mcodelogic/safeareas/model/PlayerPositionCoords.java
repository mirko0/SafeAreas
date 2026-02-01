package com.mcodelogic.safeareas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@AllArgsConstructor@NoArgsConstructor
public class PlayerPositionCoords {

    public String worldName;
    public XYZ posOne;
    public XYZ posTwo;
}
