#! /usr/bin/env python3
__author__ = 'Jie Liu'
__python_version__ = '3.4.0'

import re, urllib.request, urllib.parse, os, sys


#############
# get relevant articles from Yahoo Finance and
# store date and contents separately in .txt files
#############
def getFromYahoo(url):
    html = getHtml(url)
    # get the articles' content
    contentList = getContent(html)
    print ('Number of content: %s' %len(contentList))
    # get the titles of articles
    titleList = getTitle(html)
    print ('Number of titles: %s' %len(titleList))
    # get the publish dates
    dateList = getDate(html)
    print ('Number of dates: %s' %len(dateList))
    print ('------------------Downloading and writing start------------------')
    if len(titleList) > 0 and len(dateList) > 0 and len(contentList) > 0:        
        length = len(dateList)
        count = 0 # count the number of articles we get
        index = input ('Write to local begins at:\n') 
        for i in range(length):
            print ('Getting information from article: %s' %(i + int(index)))
            dateSuccess = False
            titleSuccess = False
            contentSuccess = False
            # write date and content of an article to a .txt file
            dateSuccess = writeDate(dateList[i], 'date')
            titleSuccess = writeTitle(titleList[i], 'title')
            contentSuccess = writeContent(contentList[i], i + int(index))
            if dateSuccess and titleSuccess and contentSuccess:
                count += 1
                print ('Done!')
            else:
                print ('Article: %s Failed' %titleList[i])
            print()
        print ('------------------Downloading and writing end------------------')
        return count
    else:
        print ('articles, titles, and dates don\'t have the same length')
    #   sys.exit()

#############
# get the html page source
#############
def getHtml(url):
    try:
        response = urllib.request.urlopen(url)
        page = response.read()
        html = page.decode('utf-8') #decode the page source
        return html
    except urllib.error.URLError as e:
        print ('URL Error: ', e)
        print ('Fail to get html from: %s' %url)
        #sys.exit()

#############
# get the titles
#############
def getTitle(html):
    titleReg = r'<div class="article_title"><h2 id="title">(.+?)</h2>'
    titleRe = re.compile(titleReg, re.S)
    try:
        titleList = titleRe.findall(html)
        # remove tags in titles
        tagReg = r'<.*?>'
        newList = []
        for t in titleList:
            t = re.sub(tagReg, '', t)
            t = t.replace('\n', ' ') # remove unnecessary line break
            newList.append(t)
        return newList
    except:
        print ('Failed to get the titles')        
        #sys.exit()

#############
# get the publish dates
#############
def getDate(html):
    dateReg = r'<b>Source Citation: </b>.+?</i>(.+?)<i>'
    dateRe = re.compile(dateReg, re.S)
    try:
        dateList = dateRe.findall(html)
        # remove tags in dates
        tagReg = r'<.*?>'
        newList = []
        for d in dateList:
            d = re.sub(tagReg, '', d)
            newList.append(d)
        return newList
    except:
        print ('Failed to get the dates!')
        #sys.exit()
        
#############
# get the content from articles
#############
def getContent(html):
    # get all paragraphs
    contentReg = r'<h2 id="title">.+?<p>(.+?)Source Citation'
    contentRe = re.compile(contentReg, re.S)
    try:
        contentList = contentRe.findall(html)
        paraReg = r'</p><p>'
        tagReg = r'<.*?>'
        list1 = []
        list2 = []
        list3 = []
        # remove \n
        for c in contentList:
            c = c.replace('\n', ' ')
            list1.append(c)
        # replace <p> to \n
        for c in list1:
            c = re.sub(paraReg, '\n', c)
            list2.append(c)
        # remove tag
        for c in list2:
            c = re.sub(tagReg, '', c)
            list3.append(c)
        return list3
    except:
        print ('Cannot get the content!')
        #sys.exit()
  
#############
# write dates to .txt files
#############
def writeDate(date, filename):
    directory = 'date'
    if not os.path.isdir(directory):
        os.mkdir(directory)
    file = directory + '\\'  + '%s.txt' % filename
    # open the file and write
    try:
        fd = open(file, 'a')
        fd.write(date)
        fd.write('\n')
        fd.close
        print ('Write article: %s\'s date done!' %filename)
        return True
    except IOError as e:
        return False
        print ('IO Error: ', e)
        #sys.exit()

#############
# write titles to .txt files
#############
def writeTitle(date, filename):
    directory = 'title'
    if not os.path.isdir(directory):
        os.mkdir(directory)
    file = directory + '\\'  + '%s.txt' % filename
    # open the file and write
    try:
        fd = open(file, 'a')
        fd.write(date)
        fd.write('\n')
        fd.close
        print ('Write article: %s\'s title done!' %filename)
        return True
    except IOError as e:
        print ('IO Error: ', e)
        return False
        #sys.exit()
        
#############
# write content to .txt files
#############
def writeContent(content, filename):
    directory = 'content'
    if not os.path.isdir(directory):
        os.mkdir(directory)
    file = directory + '\\' + '%s.txt' % filename
    #html = getHtml(article)
    #content = getContent(html)
    # open the file and write
    try:
        fd = open(file, 'w+')
        fd.write(content)
        fd.close
        print ('Write article: %s\'s content done!' %filename)
        return True
    except IOError as e:
        print ('IO Error: ', e)
        print ('Fail to write content')
        return False
        #sys.exit()
    except TypeError as te:
        print ('TypeError: ', te)
        print ('Fail to write content')
        return False
        
#############
# Delete the folder and its subfolder
#############
def delete(src):
    if os.path.isfile(src):
        try:
            os.remove(src)
        except:
            pass
    elif os.path.isdir(src):
        for file in os.listdir(src):
            filesrc = os.path.join(src, file)
            delete(filesrc)
        try:
            os.rmdir(src)
        except:
            pass
    else:
        print ('There is no folder \'%s\'\n' %src)

        
#############
# Main function
#############
while 1:
    choice = input('Enter your choice:\n1.Clean cache\n2.Start to get articles\n3.Exit the program\n')
    if choice == '1':
        delete('title')
        delete('date')
        delete('content')
        print ('Delete done\n')
    elif choice == '2':
        page = input('Please input the html file to get information:\n')
        url = 'file:///I:/Python/study2014/' + page + '.html'
        count = getFromYahoo(url)
        print ('Get and Write %s articles successfully!\n' %count)
    elif choice == '3':
        print ('Exit the program now\n')
        sys.exit()
    else:
        print ('Please enter the right choice: 1, 2 or 3\n')


