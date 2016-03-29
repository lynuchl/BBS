package com.bbs.daoImpl;

import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbs.dao.PostDao;
import com.bbs.hibernate.factory.BaseHibernateDAO;
import com.bbs.model.Followcard;
import com.bbs.model.Post;
import com.bbs.model.PostDAO;

/**
 * @author 张建浩、卜凡、卢静、余莎、姚文娜
 * @version 1.0
 * 2016年3月17日下午8:22:52
 */
public class PostDaoImpl extends BaseHibernateDAO implements PostDao{

	private static final Logger log = LoggerFactory.getLogger(PostDaoImpl.class);
	// property constants
	public static final String TITLE = "title";
	public static final String CARD_CONTENT = "cardContent";
	public static final String POST_TYPE = "postType";
	public static final String REPLY_NUM = "replyNum";
	
	private SessionFactory sessionFactory;
	
	

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	

//	public void save(Post transientInstance) {
//		log.debug("saving Post instance");
//		try {
//			Session session = getSession();
//			session.save(transientInstance);
//			
//			log.debug("save successful");
//		} catch (RuntimeException re) {
//			log.error("save failed", re);
//			throw re;
//		}
//	}

	

	public Post findById(java.lang.Integer id) {
		log.debug("getting Post instance with id: " + id);
		try {
			Session session = getSession();
			Post instance = (Post) session.get("com.bbs.model.Post", id);
			session.close();
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding Post instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Post as model where model."
					+ propertyName + "= ?";
			Session session = getSession();
			Query queryObject = session.createQuery(queryString);
			queryObject.setParameter(0, value);
			List list = queryObject.list();
			session.close();
			return list;
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByTitle(Object title) {
		return findByProperty(TITLE, title);
	}

	public List findByCardContent(Object cardContent) {
		return findByProperty(CARD_CONTENT, cardContent);
	}

	public List findByPostType(Object postType) {
		return findByProperty(POST_TYPE, postType);
	}

	public List findByReplyNum(Object replyNum) {
		return findByProperty(REPLY_NUM, replyNum);
	}

	
	@Override
	public void pushlish(Post post) {
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		session.save(post);
		transaction.commit();
		session.flush();
		session.clear();
		session.close();
	}

	
	@Override
	public List<Followcard> getFollowCards(int postId,int pageIndex,int pageSize) {
		Session session = getSession();
		String sql = "from Followcard follow where follow.post.id=?";
		Query query = session.createQuery(sql);
		query.setInteger(0, postId);
		int startIndex = (pageIndex -1) * pageSize;
		query.setFirstResult(startIndex);
		query.setMaxResults(pageSize);
		List list = query.list();
		session.flush();
		session.clear();
		session.close();
		return list;
	}
	
	
	@Override
	public Post getPostById(int postId){
		Session session = getSession();
		String sql = "from Post post where post.id=?";
		Query query = session.createQuery(sql);
		query.setInteger(0, postId);
		List<Post> posts = query.list();
		session.flush();
		session.clear();
		session.close();
		if (posts != null && posts.size()>0)
			return posts.get(0);
		return null;
	}
	
	public List<Post> search(String keyword){
		
		Session session = getSession();
		String sql = "from Post post where post.title like ?";
		Query query = session.createQuery(sql);
		query.setString(0, '%'+keyword+'%');
		List list = query.list();
		session.flush();
		session.clear();
		session.close();
		return list;
	}

	
	

	
	@Override
	public List<Post> getLatestPosts(int pageIndex, int pageSize) {
		Session session = getSession();
		String sql = "from Post p order by p.sendDate desc";
		Query query = session.createQuery(sql);
		int startIndex = (pageIndex -1) * pageSize;
		query.setFirstResult(startIndex);
		query.setMaxResults(pageSize);
		List list = query.list();
		session.flush();
		session.clear();
		session.close();
		return list;
	}


	@Override
	public List<Post> getBestPosts(int pageIndex, int pageSize) {
		Session session = getSession();
		String sql = "from Post p where p.postType = 1";
		Query query = session.createQuery(sql);
		int startIndex = (pageIndex -1) * pageSize;
		query.setFirstResult(startIndex);
		query.setMaxResults(pageSize);
		List list = query.list();
		session.flush();
		session.clear();
		session.close();
		return list;
	}
	
	public List<Post> getPostByType(int type,int pageIndex,int pageSize){
		if (type==1 || type==2 || type==3||type==4||type==5||type==6||type==7){
			Session session = getSession();
			String sql = "from Post post where post.subForum.mainForum.id=?";
			Query query = session.createQuery(sql);
			query.setInteger(0, type);
			int startIndex = (pageIndex -1) * pageSize;
			query.setFirstResult(startIndex);
			query.setMaxResults(pageSize);
			List list = query.list();
			session.flush();
			session.clear();
			session.close();
			return list;
		}else if (type==8){
			return getLatestPosts(pageIndex, pageSize);
		}
		else if (type==9){
			return getBestPosts(pageIndex, pageSize);
		}
		return null;
		
	}


	@Override
	public List<Post> getPostByUserId(int userId,int pageIndex,int pageSize) {
		Session session = getSession();
		String sql = "from Post p where p.user.id = ?";
		Query query = session.createQuery(sql);
		query.setInteger(0, userId);
		int startIndex = (pageIndex -1) * pageSize;
		query.setFirstResult(startIndex);
		query.setMaxResults(pageSize);
		List list = query.list();
		session.flush();
		session.clear();
		session.close();
		return list;
	}


	@Override
	public void autoIncreaseReply(int postId) {
//		Session session = getSession();
//		String sql = "update Post p set p.replyNum = p.replyNum+1 where p.id = ?";
//		Query query = session.createQuery(sql);
//		query.setInteger(0, postId);
//		int code = query.executeUpdate();
//		System.out.println("rows:"+code);
		Post post = findById(postId);
		post.setReplyNum(post.getReplyNum()+1);
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		session.update(post);
		transaction.commit();
		session.flush();
		session.clear();
		session.close();
		
	}
	
	public void delete(int postId){
		Post post = findById(postId);
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		session.delete(post);
		transaction.commit();
		session.flush();
		session.clear();
		session.close();
	}

	@Override
	public void updateType(Integer postId) {
		Post post = findById(postId);
		post.setPostType(1);
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		session.update(post);
		transaction.commit();
		session.flush();
		session.clear();
		session.close();
	}
	
	


	


	

}
