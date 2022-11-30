package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String CREATE_USER_SQL = "INSERT INTO myusers(firstname, lastname, age) VALUES(?, ?, ?)";
    private static final String UPDATE_USER_SQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM myusers WHERE id = ?";
    private static final String FIND_USER_BY_ID_SQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String FIND_USER_BY_NAME_SQL = "SELECT * FROM myusers WHERE firstname = ?";
    private static final String FIND_ALL_USERS_SQL = "SELECT * FROM myusers";

    public Long createUser(User user) {
        try(Connection connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(CREATE_USER_SQL)) {
                ps.setString(1, user.getFirstName());
                ps.setString(2, user.getLastName());
                ps.setInt(3, user.getAge());
                return (long) ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public User findUserById(Long userId) {
        try(Connection connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(FIND_USER_BY_ID_SQL)) {
                ps.setLong(1, userId);
                return build(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public User findUserByName(String userName) {
        try(Connection connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(FIND_USER_BY_NAME_SQL)) {
            ps.setString(1, userName);
            return build(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try(Connection connection = CustomDataSource.getInstance().getConnection();
            Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery(FIND_ALL_USERS_SQL);
            while (resultSet.next()) {
                users.add(build(resultSet));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public User updateUser(User user) {
        try(Connection connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(UPDATE_USER_SQL)) {
                ps.setString(1, user.getFirstName());
                ps.setString(2, user.getLastName());
                ps.setInt(3, user.getAge());
                ps.setLong(4, user.getId());
                if(ps.executeUpdate() != 0) {
                    return findUserById(user.getId());
                }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return new User();
    }

    public void deleteUser(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(DELETE_USER)) {
                ps.setLong(1, userId);
                ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private User build(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .firstName(resultSet.getString("firstname"))
                .lastName("lastname")
                .age(resultSet.getInt("age"))
                .build();
    }

}
