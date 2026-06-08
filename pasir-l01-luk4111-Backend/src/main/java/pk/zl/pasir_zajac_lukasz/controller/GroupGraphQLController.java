package pk.zl.pasir_zajac_lukasz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pk.zl.pasir_zajac_lukasz.dto.GroupDTO;
import pk.zl.pasir_zajac_lukasz.model.Group;
import pk.zl.pasir_zajac_lukasz.service.GroupService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GroupGraphQLController {

    private final GroupService groupService;

    @QueryMapping
    public List<Group> groups() {
        return groupService.getAllGroups();
    }

    @MutationMapping
    public Group createGroup(@Valid @Argument GroupDTO groupDTO) {
        return groupService.createGroup(groupDTO);
    }

    @MutationMapping
    public Boolean deleteGroup(@Argument Long id) {
        groupService.deleteGroup(id);
        return true;
    }
}