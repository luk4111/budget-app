package pk.zl.pasir_zajac_lukasz.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class GroupResponseDTO {
    private Long id;
    private String name;
    private Long ownerId;
}