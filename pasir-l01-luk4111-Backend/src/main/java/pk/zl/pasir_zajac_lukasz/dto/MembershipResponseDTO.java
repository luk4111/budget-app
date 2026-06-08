package pk.zl.pasir_zajac_lukasz.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MembershipResponseDTO {
    private Long id;
    private Long userId;
    private Long groupId;
    private String userEmail;
}