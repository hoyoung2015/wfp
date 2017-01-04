import smtplib
from email.mime.text import MIMEText
from email.header import Header

mail_host = 'smtp.sina.com.cn'
mail_user = 'hoyoung@sina.cn'
mail_pass = ''

sender = mail_user
receivers = ['']


message = MIMEText('发送的邮件内容包含了未被许可的信息，或被系统识别为垃圾邮件。请检查是否有用户发送病毒或者垃圾邮件','plain','utf-8')
message['From'] = sender
message['To'] = receivers[0]
message['Subject'] = Header('毕业论文数据采集出错3','utf-8')
try:
    smtpObj = smtplib.SMTP()
    smtpObj.connect(mail_host,25)
    smtpObj.login(mail_user,mail_pass)
    smtpObj.sendmail(sender,receivers,message.as_string())
    print('send email successful')
except smtplib.SMTPException as e:
    print(e)
    print('send email error')
finally:
    smtpObj.quit()