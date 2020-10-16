package com.demo.ActivitiDemo.bean.kafka;

public class XtpHeader {
	
	/**
	 * 包类型 'P'发布，'R'请求，'O'应答
	 */
	private Byte m_type = 'R';
	
	/**
	 * XTP报文的连续标志
	 */
	private Byte m_chain = 0;
	
	/**
	 * 发布会话号,每次更换会话，小于此会话号的数据将会丢弃
	 */
	private Byte m_pub_session = 0; 
	
	/**
	 * 交易前置的标识
	 */
	private Byte m_front_id = 0; 
	
	/**
	 * 除报头之外，各field长度和
	 */
	private Short m_content_length;
	
	/**
	 * 通讯阶段序号,每次更换序号，SequenceNo都从1重新开始
	 */
	private Short m_comm_phase_no = 0;
	
	/**
	 * 主题
	 */
	private Short m_subject_id = 0;
	
	/**
	 * 前置主题代码
	 */
	private Short m_front_subject_id = 0;
	
	/**
	 * XTP报文的id
	 */
	private Integer m_tid;
	
	/**
	 * XTP报文的序号
	 */
	private Integer m_sequence_no;
	
	/**
	 * 请求ID
	 */
	private Integer m_request_id;
	
	/**
	 * 请求发起者在前置的会话ID
	 */
	private Integer m_session_id;
	
	/**
	 * 在前置主题中的序号
	 */
	private Integer m_front_seq_no;
	
	/**
	 * 排队机产生的序号
	 */
	private Integer m_comp_seq_no;

	public Byte getM_type() {
		return m_type;
	}

	public void setM_type(Byte m_type) {
		this.m_type = m_type;
	}

	public Byte getM_chain() {
		return m_chain;
	}

	public void setM_chain(Byte m_chain) {
		this.m_chain = m_chain;
	}

	public Byte getM_pub_session() {
		return m_pub_session;
	}

	public void setM_pub_session(Byte m_pub_session) {
		this.m_pub_session = m_pub_session;
	}

	public Byte getM_front_id() {
		return m_front_id;
	}

	public void setM_front_id(Byte m_front_id) {
		this.m_front_id = m_front_id;
	}

	public Short getM_content_length() {
		return m_content_length;
	}

	public void setM_content_length(Short m_content_length) {
		this.m_content_length = m_content_length;
	}

	public Short getM_comm_phase_no() {
		return m_comm_phase_no;
	}

	public void setM_comm_phase_no(Short m_comm_phase_no) {
		this.m_comm_phase_no = m_comm_phase_no;
	}

	public Short getM_subject_id() {
		return m_subject_id;
	}

	public void setM_subject_id(Short m_subject_id) {
		this.m_subject_id = m_subject_id;
	}

	public Short getM_front_subject_id() {
		return m_front_subject_id;
	}

	public void setM_front_subject_id(Short m_front_subject_id) {
		this.m_front_subject_id = m_front_subject_id;
	}

	public Integer getM_tid() {
		return m_tid;
	}

	public void setM_tid(Integer m_tid) {
		this.m_tid = m_tid;
	}

	public Integer getM_sequence_no() {
		return m_sequence_no;
	}

	public void setM_sequence_no(Integer m_sequence_no) {
		this.m_sequence_no = m_sequence_no;
	}

	public Integer getM_request_id() {
		return m_request_id;
	}

	public void setM_request_id(Integer m_request_id) {
		this.m_request_id = m_request_id;
	}

	public Integer getM_session_id() {
		return m_session_id;
	}

	public void setM_session_id(Integer m_session_id) {
		this.m_session_id = m_session_id;
	}

	public Integer getM_front_seq_no() {
		return m_front_seq_no;
	}

	public void setM_front_seq_no(Integer m_front_seq_no) {
		this.m_front_seq_no = m_front_seq_no;
	}

	public Integer getM_comp_seq_no() {
		return m_comp_seq_no;
	}

	public void setM_comp_seq_no(Integer m_comp_seq_no) {
		this.m_comp_seq_no = m_comp_seq_no;
	}
	
	
}
