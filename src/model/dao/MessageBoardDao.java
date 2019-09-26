package model.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bean.Client;
import bean.MessageBoard;
import utils.Util;
import utils.jdbc.JdbcUtil;

public class MessageBoardDao {

	JdbcUtil jdbcUtil = new JdbcUtil();
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	/**
	 * 查询符合条件的留言
	 * 
	 * @param search
	 * @return
	 */
	public List<MessageBoard> listSearch(Map<String, String> search) {

		ArrayList<MessageBoard> list = new ArrayList<>();

		List<Object> searchList = new ArrayList<Object>();

		String sql = "SELECT * FROM message_board m LEFT JOIN client c ON m.board_id=c.client_id WHERE 1=1 ";

		// 创建者
		if (Util.isNotEmpty(search.get("creater"))) {

			sql += " AND c.name like concat('%',?,'%')";
			searchList.add(search.get("creater"));
		}

		// 内容
		if (Util.isNotEmpty(search.get("context"))) {

			sql += " AND m.context like concat('%',?,'%')";
			searchList.add(search.get("context"));
		}

		// 创建日期,从xx起
		if (Util.isNotEmpty(search.get("startTime"))) {
			sql += " AND m.create_time >= ? ";
			searchList.add(search.get("startTime"));
		}
		// 创建日期，止到xx
		if (Util.isNotEmpty(search.get("endTime"))) {
			sql += " AND m.create_time <= ? ";
			searchList.add(search.get("endTime"));
		}

		ResultSet rs = jdbcUtil.executeQuery(sql, searchList.toArray());

		try {
			while (rs.next()) {
				
				

				MessageBoard messageBoard = new MessageBoard();
				messageBoard.setBoardId(rs.getInt("board_id"));
				messageBoard.setContext(rs.getString("context"));
				messageBoard.setCreaterId(rs.getInt("client_id"));
				messageBoard.setCreateTime(rs.getTimestamp("create_time"));
				messageBoard.setIsActive(rs.getInt("is_active"));
				
				
				Client client = new Client();
				client.setClientId(rs.getInt("client_id"));
				client.setName(rs.getString("name"));
				client.setEmail(rs.getString("email"));
				client.setPhone(rs.getString("phone"));
				
				messageBoard.setClient(client); 
				
				list.add(messageBoard);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			jdbcUtil.close();

		}

		return list;
	}

	/*
	 * 切换留言的显示和隐藏，0隐藏，1显示
	 */
	public int toggleMessageBoardActive(String messageBoardId, String action) {

		String sql = "UPDATE message_board SET is_active=?  WHERE board_id=?";

		return jdbcUtil.executeUpdate(sql, action, messageBoardId);

	}

	/**
	 * 增加一条留言
	 * @param announcement
	 * @return
	 */
	public int addMessageBoard(MessageBoard messageBoard) {

		String sql = "INSERT INTO message_board ";
		
		sql += " ( `context`, `creater_id`, `create_time`, `is_active`) ";
		
		sql +=" VALUES (?,?,?,?,?)";
		
		return jdbcUtil.executeUpdate(sql,messageBoard.getContext(),messageBoard.getCreaterId()
				,sdf.format( messageBoard.getCreateTime() ),messageBoard.getIsActive());
	
	}

	/**
	 * 根据board_id，查询一条留言
	 * @param messageBoardId
	 * @return
	 */
	public MessageBoard getMessageBoard(int messageBoardId) {

		String sql = "SELECT * FROM message_board m LEFT JOIN client c ON m.board_id=c.clientId WHERE m.board_id=? ";
		
		ResultSet rs = jdbcUtil.executeQuery(sql, messageBoardId);
		
		MessageBoard messageBoard = null;
		
		try {
			if(rs.next()) {
				
				messageBoard = new MessageBoard();
				
				messageBoard.setBoardId(rs.getInt("board_id"));
				messageBoard.setContext(rs.getString("context"));
				messageBoard.setCreaterId(rs.getInt("client_id"));
				messageBoard.setCreateTime(rs.getTimestamp("create_time"));
				messageBoard.setIsActive(rs.getInt("is_active"));
				
				
				Client client = new Client();
				client.setClientId(rs.getInt("client_id"));
				client.setName(rs.getString("name"));
				client.setEmail(rs.getString("email"));
				client.setPhone(rs.getString("phone"));
				
				messageBoard.setClient(client); 
				
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			jdbcUtil.close();
		}
		
		return messageBoard;
	}

}
