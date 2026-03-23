length = float(input("Length (ft): "))
width = float(input("Width (ft): "))
thickness = float(input("Thickness (in): "))

area = length * width
vol = area * (thickness/12)
volCY = (vol + (0.10 * vol))/27


employees = int(input("# of employees: "))

emplTimeEst = (volCY * 3)/employees
pourTime = (volCY * 5)/60
totTime = emplTimeEst + pourTime + 1

matCost = volCY * 125
laborCost = totTime * employees * 21

total = matCost + laborCost

print(f"total time: {totTime:,.2f} hours")
print(f"total cost: ${total:,.2f}")
print(f"material cost ${matCost:,.2f}")
print(f"labor cost: {laborCost:,.2f}")

print(f"{pourTime:,.2f} hours")
print(f"{emplTimeEst:,.2f} hours")

print(f"{volCY:,.2f}")
print(f"{vol:,.2f}")
