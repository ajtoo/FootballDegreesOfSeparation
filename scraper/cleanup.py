# This file should be run against the json file created by scraper.py in post process
# It will cleanup oddities like too many spaces or newlines in the name
# One parameter is expected: path to json file

import sys
import json


def cleanup(json_filepath):
    with open(json_filepath) as json_file:
        data = json.load(json_file)  # type: dict[str, list[str]]

    cleaned_up_data = dict()  # type: dict[str, list[str]]

    for teamname, playernames in data.items():
        cleaned_up_data[teamname] = list()

        for playername in playernames:
            if playername == "\n":
                continue

            playername = playername.replace("\n", "")
            playername = playername.replace("\"", "\'")
            playername = ' '.join(playername.split())
            cleaned_up_data[teamname].append(playername)

    # write the file back out
    with open(json_filepath, 'w') as outfile:
        json.dump(cleaned_up_data, outfile)

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Wrong number of arguments: Need path to json file", file=sys.stderr)
        sys.exit(1)
    cleanup(sys.argv[1])
