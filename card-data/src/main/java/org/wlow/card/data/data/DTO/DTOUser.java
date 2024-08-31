package org.wlow.card.data.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wlow.card.data.data.PO.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTOUser{
    private Integer id;
    private String username;
    private Integer role;
    private LocalDateTime registerTime;

    public static DTOUser fromPO(User user){
        return new DTOUser(user.getId(), user.getUsername(), user.getRole().getValue(), user.getRegisterTime());
    }
}
