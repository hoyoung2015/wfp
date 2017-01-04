# -*- coding: utf-8 -*-
"""
email sender
"""
import smtplib
from email.mime.text import MIMEText
from email.header import Header
class EmailSender():
    def __init__(self,mail_host,mail_user,mail_pass):
        self.mail_host = mail_host
        self.mail_user = mail_user
        self.mail_pass = mail_pass

    def send_text(self,mail_from,mail_to,subject,content):
        message = MIMEText(content, 'plain', 'utf-8')
        message['From'] = mail_from
        message['To'] = mail_to
        message['Subject'] = Header(subject, 'utf-8')
        try:
            smtpObj = smtplib.SMTP()
            smtpObj.connect(self.mail_host, 25)
            smtpObj.login(self.mail_user, self.mail_pass)
            smtpObj.sendmail(mail_from, mail_to, message.as_string())
            print('send email successful')
        except smtplib.SMTPException as e:
            print('send email error')
            print(e)
        finally:
            smtpObj.quit()

if __name__=='__main__':
    subject = '内容包含了未被许可的信息2'
    content = '用户发送病毒或者垃圾邮件'
    emailSender = EmailSender('smtp.sina.com.cn','hoyoung@sina.cn',mail_pass='')
    emailSender.send_text('hoyoung@sina.cn','huyang09@baidu.com',subject,content)