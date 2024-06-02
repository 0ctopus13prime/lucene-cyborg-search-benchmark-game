import json

def load_json(fn):
    with open(fn, 'rt') as results:
        return json.load(results)

results = load_json('results.json')
other_results = load_json('other_engines_results.json')

command_types = {k for k in results}.union({k for k in other_results})


merged_results = {}
for command_type in command_types:
    engine_data = {}
    merged_results[command_type] = engine_data
    
    for engine in results[command_type]:
        engine_data[engine] = results[command_type][engine]
    for engine in other_results[command_type]:
        engine_data[engine] = other_results[command_type][engine]

with open('results.json', 'wt') as f:
    f.write(json.dumps(merged_results, indent=2))

