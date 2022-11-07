package dev.decagon.facebookcloneapp.dao;



import dev.decagon.facebookcloneapp.dto.PostDTO;
import dev.decagon.facebookcloneapp.exeption.EntityRepositoryExeption;
import dev.decagon.facebookcloneapp.model.Comment;
import dev.decagon.facebookcloneapp.model.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostRepositoryImpl implements PostRepository<Post, PostDTO,Integer>{


    Connection connection;

    public PostRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Post getById(Integer post_id) {

        Post post=new Post();


        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select * from post where post_id=" + post_id);

            if(rs.next())
            {
                post.setUserId(rs.getInt("user_id"));
                post.setId(rs.getInt("post_id"));
                post.setTextBody(rs.getString("text_body"));
                post.setLikes(rs.getInt("likes"));

            }
        }catch(SQLException e){
        }

        return post;
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts=new ArrayList<>();
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select * from post order by post_id desc");
            while(rs.next())
            {
                Post post=new Post();
                post.setUserId(rs.getInt("user_id"));
                post.setId(rs.getInt("post_id"));
                post.setTextBody(rs.getString("text_body"));
                post.setUserName(rs.getString("username"));
                post.setLikes(rs.getInt("likes"));
                posts.add(post);

            }
        }catch(SQLException e){
        }

        return posts;
    }

    @Override
    public Boolean save(PostDTO post) {
        String textbody= post.getTextBody();
        String username=post.getUserName();
        Integer user_id= post.getUserId();
        Integer likes= post.getLikes();
        String INSERT_USERS_SQL = "INSERT INTO post (text_body,user_id,username,likes) VALUES " + " (?, ?,?, ?);";
        Boolean result=false;
        try {
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL);

                preparedStatement.setString(1, textbody);
                preparedStatement.setInt(2, user_id);
                preparedStatement.setString(3, username);
                preparedStatement.setInt(4, likes);

                preparedStatement.executeUpdate();
                result=true;
        }catch (SQLException e){

        }
        return result;
    }

        @Override
    public Boolean update(String message, Integer postId) {

            String query = ("UPDATE post SET text_body=? WHERE post_id="+postId);
            try {
                System.out.println("before prepare statement");
                PreparedStatement stmt = connection.prepareStatement(query);
                System.out.println("after prepare statement");
                stmt.setString(1, message);
                System.out.println("The new message: "+message);
                stmt.executeUpdate();
                System.out.println("update executed");
            } catch (SQLException e) {
                throw new EntityRepositoryExeption("Update not successful");
            }
            return true;
    }

    @Override
    public Boolean delete(Integer postId) {
        int rs=0;
        try {
            PreparedStatement st = connection.prepareStatement("delete from post where post_id=" + postId);
            rs = st.executeUpdate();
        }catch (SQLException e){}
        if(rs>=1) return true;
        throw new EntityRepositoryExeption("Post with : "+postId+" not found");

    }

    @Override
    public List<Post> userPosts(Integer userId) {
        List<Post> posts=new ArrayList<>();
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select * from post where user_id="+userId);
            while(rs.next())
            {
                Post post=new Post();
                post.setId(rs.getInt("user_id"));
                post.setId(rs.getInt("post_id"));
                post.setTextBody(rs.getString("text_body"));
                post.setUserName(rs.getString("username"));
                post.setLikes(rs.getInt("likes"));
                posts.add(post);

            }
        }catch(SQLException e){
        }

        return posts;
    }

    @Override
    public Integer getPostlike(Integer postId) {
        Integer likes=0;
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select likes from post where user_id="+postId);
            if(rs.next())
            {
                likes=rs.getInt("likes");
            }
        }catch(SQLException e){
        }
        return likes;
    }
    public Integer likeAPost(Integer postId){

        String query = ("UPDATE post SET likes=? WHERE post_id="+postId);
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            int updatedLikes= getById(postId).getLikes()+1;
            stmt.setInt(1, updatedLikes);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new EntityRepositoryExeption("Update not successful");
        }
        return 1;
    }

    @Override
    public Integer unlike(Integer postId) {
        return 0;
    }

    @Override
    public List<Comment> getCommentsByPostId(Integer postId) {
        List<Comment> comments=new ArrayList<>();
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select * from comment where post_id="+postId);
            rs.next();
            while(rs.next()){
                {
                    Comment comment=new Comment();
                    comment.setCommentId(rs.getInt("comment_id"));
                    comment.setPostId(rs.getInt("post_id"));
                    comment.setTextBody(rs.getString("text_body"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setLikes(rs.getInt("likes"));

                    comments.add(comment);
                }
            }
        }catch(SQLException e){
        }

        return comments;
    }
}
