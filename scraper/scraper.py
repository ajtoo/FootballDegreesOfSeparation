import bs4
from urllib.request import urlopen

BASE_URL = "http://www.footballsquads.co.uk/"

def main():
	year_link_list = get_year_links()
	#print(year_link_list)		#[DEBUG]
	team_players_dict = {}
	#for year_link in year_link_list:
	#	team_links = get_teams_page_links(year_link)
	#	for team in team_links:
	#		team = get_team_name(team)
	#		player_list = get_players(team)
	#		team_players_dict[team] = player_list
	#		f = open(team + ".txt, 'wb')				#write a file named after the team name
	#		for player in player_list:
	#			f.write(player.encode("utf-8"))
	#			newline = bytes("\n", 'utf-8')
	#			f.write(newline)
	team_links = get_teams_page_links(year_link_list[0])
	print(team_links[0])
	print(get_team_name(team_links[0]))
	player_list = get_players(team_links[0])
	f = open('out.txt', 'wb')
	for player in player_list:
		f.write(player.encode("utf-8"))
		newline = bytes("\n", 'utf-8')
		f.write(newline)

def get_soup(url):
	page = urlopen(url)				#get page to soupify
	soup = bs4.BeautifulSoup(page, 'html.parser')
	return soup

def get_year_links():
	soup = get_soup(BASE_URL + "archive.htm")
	year_table = soup.find_all('table')[0]
	year_by_league = year_table.find_all('tr')
	year_links = []
	for entry in year_by_league:
		td_pair = entry.find_all('td')
		if len(td_pair) == 2:
			href_list = td_pair[1].find_all('a', href=True)	#get all the links in a subsection
			for link in href_list:
				year_links.append(BASE_URL + link['href'])
				#print(link['href'])	#[DEBUG]
	return year_links

def get_teams_page_links(year_link):
	soup = get_soup(year_link)
	base_link = reconstruct_url(year_link, 5)
	#print(base_link)		#[DEBUG]
	team_link_list = soup.find_all('h5')
	team_links = []
	for link in team_link_list:
		#print(link.a['href'])		#[DEBUG]
		team_links.append(base_link + "/" + link.a['href'])
	#print(team_links)		#[DEBUG]
	return team_links

def get_players(team_link):
	soup = get_soup(team_link)
	players = []
	players_list = soup.find_all('tr')
	#skip the first entry which has the "name" column header
	for player_entry in players_list[1:]:
		entry_columns = player_entry.find_all('td')
		if len(entry_columns) == 1:
			break				#if we hit the "Players no longer at this club" header, stop
		name = entry_columns[1].contents
		if len(name) != 0:
			name = entry_columns[1].contents[0]
			players.append(name)
			#print(name.encode('utf-8', 'replace'))		#[DEBUG] #Note: printing certain chars ie \u010c or \xe3 makes python unhappy :(
	return players

def get_team_name(team_link):
	"""gets the team year and name from url, used to build a dictionary of team year and player"""
	divided_url = team_link.split("/")
	year = divided_url[4]
	soup = get_soup(team_link)
	name = soup.title.contents[0].split("-")[1].lstrip().rstrip()		#rip team name out of page title...viciously
	team_name = name + "|" + year
	return team_name

def reconstruct_url(url, last_section_num):
	"""reconstructs url by sections delimited by /"""
	divided_url = url.split("/")
	reconstructed_url = divided_url[0]	#start with the first section
	for i in range(1, last_section_num):
		reconstructed_url = reconstructed_url + "/" + divided_url[i]
	return reconstructed_url

if __name__ == '__main__':
	main()
