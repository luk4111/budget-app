package pk.zl.pasir_zajac_lukasz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class GroupDTO {
    @NotBlank(message = "Nazwa grupy nie może być pusta")
    @Size(max = 100, message = "Nazwa grupy nie może przekraczać 100 znaków")
    private String name;
}